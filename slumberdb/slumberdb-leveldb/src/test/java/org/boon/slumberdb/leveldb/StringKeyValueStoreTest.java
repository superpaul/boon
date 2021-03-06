package org.boon.slumberdb.leveldb;


import org.boon.Maps;
import org.boon.Str;
import org.boon.slumberdb.entries.Entry;
import org.boon.slumberdb.KeyValueIterable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.Ok.okOrDie;


/**
 * Created by Richard on 3/30/14.
 */
public class StringKeyValueStoreTest {

    boolean ok;
    private SimpleStringKeyValueStoreLevelDB store;

    @Before
    public void setup() {


        File file = new File("target/test-data");
        file = file.getAbsoluteFile();
        file.mkdirs();
        file = new File(file, "strings.dat");
        store = new SimpleStringKeyValueStoreLevelDB(file.toString());

    }


    @After
    public void close() {
        try {
            store.close();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Test
    public void test() {
        store.put("hello",
                "world"
        );

        String world = store.load("hello");
        Str.equalsOrDie("world", world);
    }


    @Test
    public void testBulkPut() {

        Map<String, String> map = Maps.map("hello1", "hello1",
                "hello2", "hello2");


        store.putAll(map);


        String value;

        value = store.load("hello1");
        Str.equalsOrDie("hello1", value);


        value = store.load("hello2");
        Str.equalsOrDie("hello2", value);


        store.remove("hello2");
        value = store.load("hello2");
        okOrDie(value == null);
    }


    @Test
    public void testBulkRemove() {

        Map<String, String> map = Maps.map("hello1", "hello1",
                "hello2", "hello2");


        store.putAll(map);
        store.put("somethingElse", "1");


        String value;

        value = store.load("hello1");
        Str.equalsOrDie("hello1", value);


        value = store.load("hello2");
        Str.equalsOrDie("hello2", value);


        store.removeAll(map.keySet());


        value = store.load("hello1");

        ok = value == null || die();

        value = store.load("hello2");


        ok = value == null || die();


        Str.equalsOrDie("1", store.load("somethingElse"));


    }


    @Test
    public void testSearch() {
        for (int index = 0; index < 100; index++) {
            store.put("key" + index, "value" + index);
        }

        KeyValueIterable<String, String> entries = store.search("key50");
        for (Entry<String, String> entry : entries) {
            puts(entry.key(), entry.value());
        }

        entries.close();
    }


    @Test
    public void testSearch2() {
        for (int index = 0; index < 100; index++) {
            store.put("key" + index, "value" + index);
        }

        KeyValueIterable<String, String> entries = store.search("key50");
        for (Entry<String, String> entry : entries) {
            puts(entry.key(), entry.value());
        }

        entries.close();
    }


    @Test(expected = org.boon.Exceptions.SoftenedException.class)
    public void forceException() {

        store.close();
        store.close();
        store.put("key", "value");
    }


}
