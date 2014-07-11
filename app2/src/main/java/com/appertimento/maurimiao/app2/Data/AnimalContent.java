package com.appertimento.maurimiao.app2.Data;

import android.content.res.Resources;
import android.net.Uri;

import com.appertimento.maurimiao.app2.MaurimiaoApp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appinventor.ai_colombo_maurizio.Miiaaooo.R;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class AnimalContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<AnimalItem> ITEMS = new ArrayList<AnimalItem>();

    /**
     * A map of AnimalItem, by ID.
     */
    public static Map<String, AnimalItem> ITEM_MAP = new HashMap<String, AnimalItem>();
    /**
     * A list of Map AnimalItem
     */
    public static ArrayList<HashMap<String, Object>> ANIMALS_MAP=new ArrayList<HashMap<String, Object>>();

    //private static ArrayList<Integer> imageReferenceList=new ArrayList<Integer>();
    public static int getSize(){
        return ITEMS.size();
    }
    public static Uri getItem(int i){
        return ITEMS.get(i).image;
    }
    public static String getTitle(int i){
        return ITEMS.get(i).content;
    }
    private ArrayList<String>customTitles;
    private ArrayList<String>titles;

    public static void addPicture(String title,Uri imageUri){

        addItem(new AnimalItem(String.valueOf(getSize()),title,imageUri));
        /***
        customPictures.add(imageUri);
        imageReferenceList.add(imageUri);

        titles.add(title);
        customTitles.add(title);
         ****/
    }

    static  {
        /****
        // Add 3 sample items.
        //for(String title: animalList){  addItem(new AnimalItem(title,title));     }
        addItem(new AnimalItem("1", "Item 1"));
        addItem(new AnimalItem("2", "Item 2"));
        addItem(new AnimalItem("3", "Item 3"));
         ****/

        //package name
        String pack= MaurimiaoApp.getContext().getPackageName();
        //resource for this application
        Resources  res= MaurimiaoApp.getContext().getResources();
        //costruisco array con i nomi statici presi da resource
        String []animalList=res.getStringArray(R.array.pet_names);

        //scorro lista nomi
        for(int i=0;i<animalList.length;i++){
            //recuperando immagine da resource drawable
            int animalImage=res.getIdentifier(animalList[i],"drawable",pack);
            Uri path = Uri.parse("android.resource://"+pack+"/" + animalImage);
            //costruisco e aggiungo AnimalItem
            addItem(new AnimalItem(String.valueOf(i), animalList[i], path));
        }


    }

    /**
     * Metodo aggiunge AnimalItem alla lista usato internamente alla classe AnimalContent
     * @param item
     */

    private static void addItem(AnimalItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
        HashMap<String,Object> map = new HashMap<String, Object>();
        map.put("image",item.image);
        map.put("name",item.content);
        ANIMALS_MAP.add(map);
    }

    /**
     * AnimalItem rappresenta oggetto costituito da
     */
    public static class AnimalItem {
        /**
         * id identificativo animale tipicamente numero
         */
        public String id;
        /**
         * content il nome dell'animale
         */
        public String content;
        /**
         * image riferimento immagine animale
         */
        public Uri image;

        /**
         * costruttore
         * @param id  tipicamente un numero
         * @param content   tipicamente il nome
         * @param ref       riferimento immagine animale da resource
         */
        public AnimalItem(String id, String content, Uri ref) {
            this.id = id;
            this.content = content;
            image=ref;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
