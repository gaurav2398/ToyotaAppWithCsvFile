package impel.imhealthy.adminapp.QrCodeScanner;

import android.annotation.SuppressLint;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.Comparator;
import java.util.List;

public class WiFi {
    // Constants used for different security types
    public static final String WPA2 = "WPA2";
    public static final String WPA = "WPA";
    public static final String WEP = "WEP";
    public static final String OPEN = "Open";
    // For EAP Enterprise fields
    public static final String WPA_EAP = "WPA-EAP";
    public static final String IEEE8021X = "IEEE8021X";

    public static final String[] EAP_METHOD = { "PEAP", "TLS", "TTLS" };

    public static final int WEP_PASSWORD_AUTO = 0;
    public static final int WEP_PASSWORD_ASCII = 1;
    public static final int WEP_PASSWORD_HEX = 2;

    private static final String TAG = "Wifi Connecter";

    /**
     * Change the password of an existing configured network and connect to it
     * @param wifiMgr
     * @param config
     * @param newPassword
     * @return
     */
    public static boolean changePasswordAndConnect( final WifiManager wifiMgr, final WifiConfiguration config, final String newPassword, final int numOpenNetworksKept) {
        setupSecurity(config, getWifiConfigurationSecurity(config), newPassword);
        final int networkId = wifiMgr.updateNetwork(config);
        if(networkId == -1) {
            // Update failed.
            return false;
        }
        //确定
        return connectToConfiguredNetwork(wifiMgr, config, true);
    }

    public static boolean connectToNewNetwork(final WifiManager wifiMgr, final ScanResult scanResult, final String password) {
        //1.获取wifi加密方式（WEP, WPA, WPA2, WPA_EAP, IEEE8021X）
        final String security = getScanResultSecurity(scanResult);

        if(security.equals(OPEN)) {
            final int numOpenNetworksKept = 10;
            checkForExcessOpenNetworkAndSave(wifiMgr, numOpenNetworksKept);
        }

        WifiConfiguration config = new WifiConfiguration();
        config.SSID = StringUtils.convertToQuotedString(scanResult.SSID);
        config.BSSID = scanResult.BSSID;
        setupSecurity(config, security, password);

        int id = wifiMgr.addNetwork(config);
        if(id == -1) {
            return false;
        }

        if(!wifiMgr.saveConfiguration()) {
            return false;
        }

        config = getWifiConfiguration(wifiMgr, config, security);
        if(config == null) {
            return false;
        }

        return connectToConfiguredNetwork(wifiMgr, config, true);
    }


    public static boolean connectToConfiguredNetwork(final WifiManager wifiMgr, WifiConfiguration config, boolean reassociate) {
        final String security = getWifiConfigurationSecurity(config);

        int oldPri = config.priority;
        // Make it the highest priority.
        int newPri = getMaxPriority(wifiMgr) + 1;
        if(newPri > MAX_PRIORITY) {
            newPri = shiftPriorityAndSave(wifiMgr);
            config = getWifiConfiguration(wifiMgr, config, security);
            if(config == null) {
                return false;
            }
        }

        // Set highest priority to this configured network
        config.priority = newPri;
        int networkId = wifiMgr.updateNetwork(config);
        if(networkId == -1) {
            return false;
        }

        // Do not disable others
        if(!wifiMgr.enableNetwork(networkId, false)) {
            config.priority = oldPri;
            return false;
        }

        if(!wifiMgr.saveConfiguration()) {
            config.priority = oldPri;
            return false;
        }

        // We have to retrieve the WifiConfiguration after save.
        config = getWifiConfiguration(wifiMgr, config, security);
        if(config == null) {
            return false;
        }

        // Disable others, but do not save.
        // Just to force the WifiManager to connect to it.
        if(!wifiMgr.enableNetwork(config.networkId, true)) {
            return false;
        }

        final boolean connect = reassociate ? wifiMgr.reassociate() : wifiMgr.reconnect();
        if(!connect) {
            return false;
        }

        return true;
    }

    private static void sortByPriority(final List<WifiConfiguration> configurations) {
        java.util.Collections.sort(configurations, new Comparator<WifiConfiguration>() {

            @Override
            public int compare(WifiConfiguration object1,
                               WifiConfiguration object2) {
                return object1.priority - object2.priority;
            }
        });
    }

    private static boolean checkForExcessOpenNetworkAndSave(final WifiManager wifiMgr, final int numOpenNetworksKept) {
        @SuppressLint("MissingPermission") final List<WifiConfiguration> configurations = wifiMgr.getConfiguredNetworks();
        sortByPriority(configurations);

        boolean modified = false;
        int tempCount = 0;
        for(int i = configurations.size() - 1; i >= 0; i--) {
            final WifiConfiguration config = configurations.get(i);
            if(getWifiConfigurationSecurity(config).equals(OPEN)) {
                tempCount++;
                if(tempCount >= numOpenNetworksKept) {
                    modified = true;
                    wifiMgr.removeNetwork(config.networkId);
                }
            }
        }
        if(modified) {
            return wifiMgr.saveConfiguration();
        }

        return true;
    }

    private static final int MAX_PRIORITY = 99999;

    private static int shiftPriorityAndSave(final WifiManager wifiMgr) {
        @SuppressLint("MissingPermission") final List<WifiConfiguration> configurations = wifiMgr.getConfiguredNetworks();
        sortByPriority(configurations);
        final int size = configurations.size();
        for(int i = 0; i < size; i++) {
            final WifiConfiguration config = configurations.get(i);
            config.priority = i;
            wifiMgr.updateNetwork(config);
        }
        wifiMgr.saveConfiguration();
        return size;
    }

    private static int getMaxPriority(final WifiManager wifiManager) {
        @SuppressLint("MissingPermission") final List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        int pri = 0;
        for(final WifiConfiguration config : configurations) {
            if(config.priority > pri) {
                pri = config.priority;
            }
        }
        return pri;
    }

    public static WifiConfiguration getWifiConfiguration(final WifiManager wifiMgr,
                                                         final ScanResult hotsopt, String hotspotSecurity) {
        final String ssid = StringUtils.convertToQuotedString(hotsopt.SSID);
        if(ssid.length() == 0) {
            return null;
        }

        final String bssid = hotsopt.BSSID;
        if(bssid == null) {
            return null;
        }

        if(hotspotSecurity == null) {
            hotspotSecurity = getScanResultSecurity(hotsopt);
        }

        @SuppressLint("MissingPermission") final List<WifiConfiguration> configurations = wifiMgr.getConfiguredNetworks();

        for(final WifiConfiguration config : configurations) {
            if(config.SSID == null || !ssid.equals(config.SSID)) {
                continue;
            }
            if(config.BSSID == null || bssid.equals(config.BSSID)) {
                final String configSecurity = getWifiConfigurationSecurity(config);
                if(hotspotSecurity.equals(configSecurity)) {
                    return config;
                }
            }
        }
        return null;
    }

    public static WifiConfiguration getWifiConfiguration(final WifiManager wifiMgr,
                                                         final WifiConfiguration configToFind, String security) {
        final String ssid = configToFind.SSID;
        if(ssid.length() == 0) {
            return null;
        }

        final String bssid = configToFind.BSSID;


        if(security == null) {
            security = getWifiConfigurationSecurity(configToFind);
        }

        @SuppressLint("MissingPermission") final List<WifiConfiguration> configurations = wifiMgr.getConfiguredNetworks();

        for(final WifiConfiguration config : configurations) {
            if(config.SSID == null || !ssid.equals(config.SSID)) {
                continue;
            }
            if(config.BSSID == null || bssid == null || bssid.equals(config.BSSID)) {
                final String configSecurity = getWifiConfigurationSecurity(config);
                if(security.equals(configSecurity)) {
                    return config;
                }
            }
        }
        return null;
    }

    /**
     * @return The security of a given {@link WifiConfiguration}.
     */
    static public String getWifiConfigurationSecurity(WifiConfiguration wifiConfig) {

        if (wifiConfig.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE)) {
            // If we never set group ciphers, wpa_supplicant puts all of them.
            // For open, we don't set group ciphers.
            // For WEP, we specifically only set WEP40 and WEP104, so CCMP
            // and TKIP should not be there.
            if (!wifiConfig.allowedGroupCiphers.get(WifiConfiguration.GroupCipher.CCMP)
                    && (wifiConfig.allowedGroupCiphers.get(WifiConfiguration.GroupCipher.WEP40)
                    || wifiConfig.allowedGroupCiphers.get(WifiConfiguration.GroupCipher.WEP104))) {
                return WEP;
            } else {
                return OPEN;
            }
        } else if (wifiConfig.allowedProtocols.get(WifiConfiguration.Protocol.RSN)) {
            return WPA2;
        } else if (wifiConfig.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP)) {
            return WPA_EAP;
        } else if (wifiConfig.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return IEEE8021X;
        } else if (wifiConfig.allowedProtocols.get(WifiConfiguration.Protocol.WPA)) {
            return WPA;
        } else {
            Log.w(TAG, "Unknown security type from WifiConfiguration, falling back on open.");
            return OPEN;
        }
    }

    static private void setupSecurity(WifiConfiguration config, String security, final String password) {
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        if (TextUtils.isEmpty(security)) {
            security = OPEN;
            Log.w(TAG, "Empty security, assuming open");
        }

        if (security.equals(WEP)) {
            int wepPasswordType = WEP_PASSWORD_AUTO;
            // If password is empty, it should be left untouched
            if (!TextUtils.isEmpty(password)) {
                if (wepPasswordType == WEP_PASSWORD_AUTO) {
                    if (isHexWepKey(password)) {
                        config.wepKeys[0] = password;
                    } else {
                        config.wepKeys[0] = StringUtils.convertToQuotedString(password);
                    }
                } else {
                    config.wepKeys[0] = wepPasswordType == WEP_PASSWORD_ASCII
                            ? StringUtils.convertToQuotedString(password)
                            : password;
                }
            }

            config.wepTxKeyIndex = 0;

            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);

            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

        } else if (security.equals(WPA) || security.equals(WPA2)){
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);

            config.allowedProtocols.set(security.equals(WPA2) ? WifiConfiguration.Protocol.RSN : WifiConfiguration.Protocol.WPA);

            // If password is empty, it should be left untouched
            if (!TextUtils.isEmpty(password)) {
                if (password.length() == 64 && isHex(password)) {
                    // Goes unquoted as hex
                    config.preSharedKey = password;
                } else {
                    // Goes quoted as ASCII
                    config.preSharedKey = StringUtils.convertToQuotedString(password);
                }
            }

        } else if (security.equals(OPEN)) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (security.equals(WPA_EAP) || security.equals(IEEE8021X)) {
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            if (security.equals(WPA_EAP)) {
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
            } else {
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
            }
            if (!TextUtils.isEmpty(password)) {
                config.preSharedKey = StringUtils.convertToQuotedString(password);
            }
        }
    }

    private static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();

        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        return isHex(wepKey);
    }

    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f')) {
                return false;
            }
        }

        return true;
    }

    static final String[] SECURITY_MODES = { WEP, WPA, WPA2, WPA_EAP, IEEE8021X };

    /**
     * @return The security of a given {@link ScanResult}.
     */
    public static String getScanResultSecurity(ScanResult scanResult) {
        final String cap = scanResult.capabilities;
        for (int i = SECURITY_MODES.length - 1; i >= 0; i--) {
            if (cap.contains(SECURITY_MODES[i])) {
                return SECURITY_MODES[i];
            }
        }

        return OPEN;
    }
}
