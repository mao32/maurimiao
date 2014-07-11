package com.appertimento.maurimiao.app2;

/**
 * Created by mcolombo on 22/03/14.
 */


/********
 public class Utils {
    //private  ArrayList<AssetManager.AssetInputStream> stdPictures =new ArrayList<AssetManager>();
    private  ArrayList<Uri> customPictures ;//=new ArrayList<Uri>();
    private static ArrayList<Object> objList;
    private ArrayList<String>customTitles;
    private ArrayList<String>titles;

    private static Utils istance=null;

    private Utils(String[] stdTitle,String[] cstTitle,ArrayList<AssetManager.AssetInputStream> std, ArrayList<Uri> cst) {
        //stdPictures=std;
        customTitles=new ArrayList<String>(Arrays.asList(cstTitle));
        titles=new ArrayList<String>(Arrays.asList(stdTitle));
        titles.addAll(customTitles);

        customPictures=cst;
        objList=new ArrayList<Object>(std);
        objList.addAll(cst);
    }


    public static Utils getInstance(String[] stdTitle,String[] cstTitle,ArrayList<AssetManager.AssetInputStream> std, ArrayList<Uri> cst){
        if(istance==null)
            istance=new Utils(stdTitle,cstTitle,std,cst);

        return istance;
    }

    public static Utils getInstance() throws Throwable {
        if(istance==null)
            throw new Throwable("istance is null");

        return istance;
    }

    public Object getItem(int i){
        return objList.get(i);
    }

    public int getSize(){
        return objList.size();
    }

    void addPicture(String title,Uri imageUri){
        customPictures.add(imageUri);
        objList.add(imageUri);

        titles.add(title);
        customTitles.add(title);
    }

    String getTitle(int i){
        return titles.get(i);
    }
}
*********/