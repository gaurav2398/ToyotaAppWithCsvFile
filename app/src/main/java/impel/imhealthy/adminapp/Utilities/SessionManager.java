package impel.imhealthy.adminapp.Utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import impel.imhealthy.adminapp.PinCodeActivity;


public class SessionManager {

    Context context;
    public static final String KEY_MOVIE_ID="movieid";
    public static final String KEY_USERID="userid";
    public static final String KEY_STATENAME="statename";
    public static final String KEY_USERNAME="username";
    public static final String KEY_EMAIL="email";
    private static final String KEY_PASSWORD= "password";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String IS_Lock ="Islocked";
    public static final String KEY_ENAME ="ename";
    public static final String KEY_EADD ="eadd";
    public static final String IS_Prpfile_Image ="profileimg";
    public static final String POSITION ="profileimg";
    public static final String MOBILE ="mobile";
    public static final String NAME ="name";
    public static final String IS_Prpfile_Submit ="submit";
    private static final String Pref_Name= "Login";
    SharedPreferences.Editor editor;
    SharedPreferences pref;

    Context _context;
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public SessionManager(Context context1) {
        this.context=context1;
        pref = context.getSharedPreferences(Pref_Name, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String userid){

        editor.putBoolean(IS_LOGIN,true);
        editor.putString(KEY_USERID, userid);

        editor.commit();
    }
    public void savestatename(String statename){
        editor.putString(KEY_STATENAME, statename);
        editor.commit();
    }
    public void savename(String username){
        editor.putString(KEY_USERNAME, username);
        editor.commit();
    }
    public void imgs(String imgurl){
        editor.putString(IS_Prpfile_Image, imgurl);
        editor.commit();
    }

    public void imgs(String imgurl, String mobile, String name){
        editor.putString(POSITION, imgurl);
        editor.putString(MOBILE, mobile);
        editor.putString(NAME, name);
        editor.commit();
    }
    public void createEstablishmentSession(String ename,String eaddress){
        editor.putString(KEY_ENAME, ename);
        editor.putString(KEY_EADD, eaddress);
        editor.commit();
    }
    public void profile(String imgurl){

        editor.putBoolean(IS_Prpfile_Submit,true);

        editor.commit();
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(POSITION, pref.getString(POSITION, ""));
        user.put(MOBILE, pref.getString(MOBILE, ""));
        user.put(NAME, pref.getString(NAME, ""));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, ""));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, ""));
        user.put(KEY_USERID, pref.getString(KEY_USERID, ""));
        user.put(KEY_MOVIE_ID, pref.getString(KEY_MOVIE_ID, ""));
        user.put(IS_Prpfile_Image, pref.getString(IS_Prpfile_Image, ""));
        user.put(KEY_STATENAME, pref.getString(KEY_STATENAME, ""));
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, ""));
        user.put(KEY_ENAME, pref.getString(KEY_ENAME, ""));
        user.put(KEY_EADD, pref.getString(KEY_EADD, ""));

        user.put(IS_Lock,pref.getString(IS_Lock,""));

        return user;
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();

        Intent i = new Intent(context, PinCodeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
    public boolean isLoggedInLang(){
        return pref.getBoolean(IS_Prpfile_Submit, false);
    }
}
