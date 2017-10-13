package mahlabs.mapsfriends.connection;

import android.util.JsonWriter;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by 13120dde on 2017-10-12.
 */

public class JsonHandler {

    private static String LOG="JsonHandler";

    public static String registration(String group, String userName) throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jWriter = new JsonWriter(writer);
        jWriter.beginObject()
                .name("type").value("register")
                .name("group").value(group)
                .name("member").value(userName)
                .endObject();

        Log.d(LOG,writer.toString());
        return writer.toString();
    }

    public static String deregistration(String id) throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jWriter = new JsonWriter(writer);
        jWriter.beginObject()
                .name("type").value("unregister")
                .name("id").value(id)
                .endObject();
        Log.d(LOG,writer.toString());
        return writer.toString();
    }

    public static String membersInGroup(String groupName) throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jWriter = new JsonWriter(writer);
        jWriter.beginObject()
                .name("type").value("members")
                .name("group").value(groupName)
                .endObject();
        Log.d(LOG,writer.toString());
        return writer.toString();
    }

    public static String currentGroups() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jWriter = new JsonWriter(writer);
        jWriter.beginObject()
                .name("type").value("groups")
                .endObject();
        Log.d(LOG,writer.toString());
        return writer.toString();
    }

    public static String setPosition(String id, double longitude, double latitude) throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jWriter = new JsonWriter(writer);
        jWriter.beginObject()
                .name("type").value("location")
                .name("id").value(id)
                .name("longitude").value(""+longitude)
                .name("latitude").value(""+latitude)
                .endObject();
        Log.d(LOG,writer.toString());
        return writer.toString();
    }

    public static String enterTextMsg(int id, String text) throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jWriter = new JsonWriter(writer);
        jWriter.beginObject()
                .name("type").value("textchat")
                .name("id").value(id)
                .name("text").value(text)
                .endObject();
        Log.d(LOG,writer.toString());
        return writer.toString();
    }

    public static String enterImageMsg(int id, String text,double longitude, double latitude) throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jWriter = new JsonWriter(writer);
        jWriter.beginObject()
                .name("type").value("imagechat")
                .name("id").value(id)
                .name("text").value(text)
                .name("longitude").value(longitude)
                .name("latitude").value(latitude)
                .endObject();
        Log.d(LOG,writer.toString());
        return writer.toString();
    }

}
