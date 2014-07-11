package com.appertimento.maurimiao.app2;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.appertimento.maurimiao.app2.Data.AnimalContent;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import appinventor.ai_colombo_maurizio.Miiaaooo.R;


//public class AnimalListActivity extends ActionBarActivity {
public class AnimalListActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_animal_list);
        //String[] animals = getResources().getStringArray(R.array.pet_names);
        //setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,animals));
        ArrayList<Map<String, Object>> animalesList = new ArrayList<Map<String, Object>>();

       // String[] petTitle=getResources().getStringArray(R.array.pet_names);
/*****
        animalesList.add(createPet(petTitle[0],getResources().getDrawable(R.drawable.kitty)));
        animalesList.add(createPet(petTitle[1],getResources().getDrawable(R.drawable.shrek)));
        animalesList.add(createPet(petTitle[2],getResources().getDrawable(R.drawable.maiale)));
      ******V2***/

        /*****
        animalesList.add(createPet(petTitle[0],R.drawable.kitty));
        animalesList.add(createPet(petTitle[1],R.drawable.shrek));
        animalesList.add(createPet(petTitle[2],R.drawable.maiale));
********/
        /******
        Utils utils= null;
        try {
            utils = Utils.getInstance();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        for(int i=0;i<utils.getSize();i++)
            animalesList.add(createPet(utils.getTitle(i), utils.getItem(i)));
        **********/
        for(int i=0;i< AnimalContent.getSize();i++)
            animalesList.add(createPet(AnimalContent.getTitle(i), AnimalContent.getItem(i)));
        SimpleAdapter adapter=new SimpleAdapter(getBaseContext(),animalesList, R.layout.activity_animal_list,new String[]{"image","name"},new int[]{R.id.img_thumbnail,R.id.title});
        SimpleAdapter.ViewBinder viewBinder=new SimpleAdapter.ViewBinder(){

            @Override
            public boolean setViewValue(View view, Object o, String s) {
                if(view.getId()==R.id.img_thumbnail && o.getClass()== AssetManager.AssetInputStream.class){
                    Log.i("MyApplication", s);
                    ImageView imgView=(ImageView) view;
                    imgView.setImageBitmap(BitmapFactory.decodeStream((InputStream) o));
                    return true;
                }
                return false;
            }
        };
        adapter.setViewBinder(viewBinder);
        setListAdapter(adapter);


    }


    private HashMap<String, Object> createPet(String key, Object name) {

        HashMap<String, Object> pet = new HashMap<String, Object>();


        pet.put("image", name);
        pet.put("name", key);

        return pet;

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent=getIntent();
        intent.putExtra("position",position);
        setResult(RESULT_OK,intent);

        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.animal_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
