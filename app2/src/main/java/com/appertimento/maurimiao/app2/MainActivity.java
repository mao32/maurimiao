package com.appertimento.maurimiao.app2;


import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.LruCache;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appertimento.maurimiao.app2.Data.AnimalContent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import appinventor.ai_colombo_maurizio.Miiaaooo.R;


public class MainActivity extends ActionBarActivity implements AnimalListFragment.OnFragmentInteractionListener{
//public class MainActivity2 extends Activity {

    public static final String TAG="com.appertimento.maurimiao";
    static final int REQUEST_IMAGE_CAPTURE = 99;
    static final String PHOTO_URI="PHOTO_URI";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    Uri photoUri;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    AnimalListFragment mAnimalListFragment;

    ShakeDetector mShakeDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"MainActivity - onCreate");

        setContentView(R.layout.activity_main);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Button chooseButton = (Button) findViewById(R.id.chooseButton);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mAnimalListFragment=(AnimalListFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                int current=mViewPager.getCurrentItem();
                //chooseButton.setText(mSectionsPagerAdapter.utils.getTitle(current));
                chooseButton.setText(AnimalContent.getTitle(current));
                //Toast.makeText(getBaseContext(),"swipe "+ mSectionsPagerAdapter.utils.getTitle(current),Toast.LENGTH_SHORT).show();
            }
        });


        //Spinner mSpinner= (Spinner) rootView.findViewById(R.id.spinner);
        //ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getActivity(), R.array.pet_names, android.R.layout.simple_spinner_item);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //mSpinner.setAdapter(adapter);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(),AnimalListActivity.class);
                startActivityForResult(intent,0);
            }
        });

        Button btnLoad= (Button) findViewById(R.id.loadPicture);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
        Button btnTake=(Button) findViewById(R.id.takePicture);
        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    photoUri=getOutputMediaFileUri();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                }
            }
        });

////DRAWER SECTION////////
        mDrawerLayout = (DrawerLayout) findViewById(R.id.draw_layout);


        mDrawerToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.drawable.ic_drawer,R.string.navigation_drawer_open,R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //if(!isAdded()) return ;
                //chiamo menu x modificare i pulsanti della barra
                getSupportActionBar().setTitle(mSectionsPagerAdapter.getPageTitle(mViewPager.getCurrentItem()));
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //chimo menu x modificare i pulsanti sulla barra
                getSupportActionBar().setTitle("SELEZIONA ANIMALE");
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);


        mShakeDetector=new ShakeDetector(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                Toast.makeText(MainActivity.this,"Shaking",Toast.LENGTH_LONG).show();
            }
        });

     }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(photoUri!=null) outState.putString(PHOTO_URI,photoUri.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey(PHOTO_URI))
           photoUri=Uri.parse(savedInstanceState.getString(PHOTO_URI));
    }

    @Override
    protected void onStart() {
        Log.i(TAG,"MainActivity - onStart");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.i(TAG,"MainActivity - onPause");
        super.onPause();
        mShakeDetector.stopListening();
    }

    @Override
    protected void onStop() {
        Log.i(TAG,"MainActivity - onStop");
        super.onStop();

    }

    @Override
    protected void onResume() {
        Log.i(TAG,"MainActivity - onResume");
        super.onResume();
        mShakeDetector.startListening();
    }

    @Override
    protected void onRestart() {
        Log.i(TAG,"MainActivity - onRestart");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG,"MainActivity - onDestroy");
        super.onDestroy();
    }

    private static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {
        Log.d(TAG, "getOutputMediaFile() type:" );
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        // For future implementation: store videos in a separate directory
        //File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),  "maurimiao");
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile;
        //if (type == MEDIA_TYPE_IMAGE) {
        mediaFile = new File(mediaStorageDir.getPath() + File.separator  + "IMG_" + timeStamp + ".jpg");
            /*} else if (type == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + "VID_" + timeStamp + ".mp4");
            } else if (type == MEDIA_TYPE_AUDIO) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + "AUD_" + timeStamp + ".3gp");
            } else {
                Log.e(LOG_TAG, "typ of media file not supported: type was:" + type);
                return null;
            }*/

        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==0){//scelta animale da pick
                mViewPager.setCurrentItem(data.getIntExtra("position",0));
            }
            else if(requestCode==1) {//load animale picture
                Uri imageUri = data.getData();
                mSectionsPagerAdapter.add(imageUri);

                try {
                    //mViewPager.setCurrentItem(Utils.getInstance().getSize()-1);
                    mViewPager.setCurrentItem(AnimalContent.getSize()-1);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                Log.i(TAG, imageUri.toString());
            }
            else if(requestCode==REQUEST_IMAGE_CAPTURE){
               // Bundle bundle = data.getExtras();
                //Uri imageUri=(Uri)bundle.get(MediaStore.EXTRA_OUTPUT);
                //if(imageUri==null) Log.i(TAG,"PHOTO URI IS NULL");
                //Log.i(TAG,imageUri.toString());
                if(photoUri==null) {
                    Log.i(TAG, "EXCEPTION - photoURI is null");
                    Toast.makeText(this,"Photo is missing please take again",Toast.LENGTH_LONG);
                    return;
                }
                mSectionsPagerAdapter.add(photoUri);
                mViewPager.setCurrentItem(AnimalContent.getSize()-1);
                Log.i(TAG, photoUri.toString());
            }
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (mDrawerToggle.onOptionsItemSelected(item)) return true;

            int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(this,"action sample",Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(int pos) {
        mViewPager.setCurrentItem(pos);
        mDrawerLayout.closeDrawer(findViewById(R.id.navigation_drawer));

    }


    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        //Utils utils;
/*******non dovrebbe servire*********
        void init() {
            ArrayList<AssetManager.AssetInputStream>stdPictures=new ArrayList<AssetManager.AssetInputStream>();
            String[] stdPetImages=getResources().getStringArray(R.array.pet_images);
            for(String imageFile : stdPetImages){
                //try {
                AssetManager.AssetInputStream is = null;
                try {
                    is = (AssetManager.AssetInputStream) getResources().getAssets().open(imageFile+".png");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                stdPictures.add(is);


                Log.i(TAG,"image "+imageFile+" generated");

                //} catch (IOException e) { e.printStackTrace();     }
            }
            String[] stdTitle=getResources().getStringArray(R.array.pet_names);
            utils=Utils.getInstance(stdTitle,new String[0],stdPictures,new ArrayList<Uri>());
        }
 **************/

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            //init();
            //Utils.init();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            try {
                //return PlaceholderFragment.newInstance(position + 1,utils.getItem(position));
                Log.i(TAG,"getItem - Callig AnimalContent.getItem(position) at position "+position);
                if(AnimalContent.getItem(position)==null) Log.i(TAG, "Animal is null");
                return PlaceholderFragment.newInstance(position + 1, AnimalContent.getItem(position));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void add(Uri t){
            //utils.addPicture("custom...",t);
            AnimalContent.addPicture("custom...", t);
            Log.i(TAG, "Image added");
            notifyDataSetChanged();
            mAnimalListFragment.mSAdapter.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            //return utils.getSize();
            return AnimalContent.getSize();
        }

        /**
         * Ritorna il titolo dell'item corrente il nome dell'animale x esempio
         * @param position
         * @return
         */
        @Override
        public CharSequence getPageTitle(int position) {
            //return utils.getTitle(position);
            return AnimalContent.getTitle(position);
            /*****
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
             ******/
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String IMAGE="image";
        private ImageLoader imageLoader=new ImageLoader();
        //private static final ImageLoader imageLoader = new ImageLoader();
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, Object picture) throws IOException {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            //BufferedInputStream buf=new BufferedInputStream(pictureStream);
            if(picture==null){
                Log.i(TAG,"newInstance picture is null");
                return null;
            }
            if(picture.getClass()== AssetManager.AssetInputStream.class) {
                AssetManager.AssetInputStream pictureStream=(AssetManager.AssetInputStream) picture;
                //byte[] bMapArray = new byte[pictureStream.available()];
                //pictureStream.read(bMapArray);
                Bitmap bmp=BitmapFactory.decodeStream(pictureStream);
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG,100,stream);
                byte[] bMapArray = stream.toByteArray();
                args.putByteArray(IMAGE, bMapArray);
            }
            else if(picture instanceof Uri){
                Log.i(TAG, "add uri " + picture.toString());
                //scrive gli argomenti che verranno passati al metodo onCreateView
                args.putString(IMAGE, picture.toString());
            }

            else          Log.i(TAG, "no arguments " + picture.getClass().toString()+ " vs "+Uri.class.toString());
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);


            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));

            ImageView petImage= (ImageView) rootView.findViewById(R.id.petImage);
            //InputStream is = null;
            //is = getResources().getAssets().open(getArguments().getString("image")+".png");
            Log.i(TAG,"get image");
            //petImage.setImageBitmap(BitmapFactory.decodeStream(is));
            Object obj=getArguments().get(IMAGE);
            Log.i(TAG,"object "+obj.toString());
            /**
            if(obj.getClass().isArray()) {
                byte[] arr = getArguments().getByteArray(IMAGE);
                petImage.setImageBitmap(BitmapFactory.decodeByteArray(arr, 0, arr.length));
            }
            else**/

            if(obj.getClass()==String.class) {
             //   petImage.setImageURI((Uri.parse(getArguments().getString(IMAGE))));

                try {

                    //get original inputstream
                    Uri imageUri=Uri.parse(getArguments().getString(IMAGE));
                    InputStream inputImageStream=container.getContext().getContentResolver().openInputStream(imageUri);
                    //use cursor to get orientation
                    /***
                    Cursor cursor=container.getContext().getContentResolver().query(Uri.parse(getArguments().getString(IMAGE)),new String[]{MediaStore.Images.ImageColumns.ORIENTATION},null,null,null);
                    if(cursor==null)       Log.i(TAG,"CURSOR IS NULL");

                    if(cursor!=null && cursor.getCount()==1) cursor.moveToFirst();
                     int orientation=cursor.getInt(0);
                    ********/
                    ExifInterface exifMedia = null;
                    try {
                        exifMedia = new ExifInterface(imageUri.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String sOrientation=exifMedia.getAttribute(ExifInterface.TAG_ORIENTATION);
                    int orientation=Integer.parseInt(sOrientation);
                    Log.i(TAG,"ORIENTATION "+sOrientation);
                    imageLoader.loadBitmap(inputImageStream,petImage,getArguments().getString(IMAGE),orientation);
                    /***
                    BitmapWorkerTask task = new BitmapWorkerTask(petImage);
                    task.execute(inputImageStream);
                    ****/
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
            else {
                Log.e(TAG, "URI is empty", new Throwable("URI IS EMPTY"));
            }

            //petImage.setImageURI(Uri.parse("file:///android_asset/kitty.png"));

            return rootView;
        }





        @Override
        public void onDestroy() {
            Log.i(TAG," fragment onDestroy");
            super.onDestroy();
        }


    }

}
