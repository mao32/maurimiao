package com.appertimento.maurimiao.app2;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.appertimento.maurimiao.app2.Data.AnimalContent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import appinventor.ai_colombo_maurizio.Miiaaooo.R;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class AnimalListFragment extends Fragment implements AbsListView.OnItemClickListener {
    static final String TAG="AnimalListFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;
    SimpleAdapter mSAdapter;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    //private ListAdapter mAdapter;
    //private SimpleAdapter mSAdapter;

    private DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    int  mCurrentSelectedPosition=0;

    // TODO: Rename and change types of parameters
    public static AnimalListFragment newInstance(String param1, String param2) {
        AnimalListFragment fragment = new AnimalListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AnimalListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // TODO: Change Adapter to display your content
        //getResources().getIdentifier("kitty","drawable",getActivity().getPackageName());

        //AnimalContent.init(getResources().getStringArray(R.array.pet_names,Resources));
        //AnimalContent.init(getResources(),getActivity().getPackageName());

        //mAdapter = new ArrayAdapter<AnimalContent.AnimalItem>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, AnimalContent.ITEMS);
        mSAdapter=new SimpleAdapter(getActivity(), AnimalContent.ANIMALS_MAP, R.layout.activity_animal_list,new String[]{"image","name"},new int[]{R.id.img_thumbnail,R.id.title});

        SimpleAdapter.ViewBinder viewBinder=new SimpleAdapter.ViewBinder(){
            ImageLoader imageLoader=new ImageLoader();
            @Override
            public boolean setViewValue(View view, Object o, String s) {
                if(view.getId()== R.id.img_thumbnail ){
                    Log.i(TAG, "setViewValue "+s);
                    ImageView imgView=(ImageView) view;
                    //imgView.setImageBitmap(BitmapFactory.decodeStream((InputStream) o));
                    try {
                        //get original inputstream
                        InputStream inputImageStream=getActivity().getContentResolver().openInputStream((Uri)o);
                        /***GET ORIENTATION BY CURSOR
                        Cursor cursor=getActivity().getContentResolver().query((Uri)o,new String[]{MediaStore.Images.ImageColumns.ORIENTATION},null,null,null);
                        if(cursor.getCount()==1) cursor.moveToFirst();
                        imageLoader.loadBitmap(inputImageStream,imgView,s,cursor.getInt(0));
                         ********/
                        ExifInterface exifMedia = null;
                        Uri imageUri=(Uri)o;
                        try {
                            exifMedia = new ExifInterface(imageUri.getPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String sOrientation=exifMedia.getAttribute(ExifInterface.TAG_ORIENTATION);
                        int orientation=Integer.parseInt(sOrientation);
                        Log.i(TAG,"ORIENTATION "+sOrientation);

                        imageLoader.loadBitmap(getActivity().getContentResolver().openInputStream((Uri)o),imgView,s,orientation);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        };
        mSAdapter.setViewBinder(viewBinder);
        setRetainInstance(true);
        //selectItem(mCurrentSelectedPosition);

    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        //mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
/***
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
*****/
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }
                /******PARTE RELATIVA ALLE PREFERENCES
                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).commit();
                }
                ***************/
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        /******PREFERECES*****
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }
******************/
        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_animallistfragment, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        //((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        mListView.setAdapter(mSAdapter);
       // mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);


        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        Log.i("AnimalListFragment", "onCreateView - calling setitemchecked");
        mListView.setItemChecked(mCurrentSelectedPosition, true);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("AnimalListFragment", "onItemClick - calling selectItem");
        selectItem(position);

    }

    public void selectItem(int position){
        mCurrentSelectedPosition = position;
        if (mListView != null) {
            mListView.setItemChecked(position, true);
        }
/****
 if (mDrawerLayout != null) {
 mDrawerLayout.closeDrawer(mFragmentContainerView);
 }
 ***/
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            //mListener.onFragmentInteraction(AnimalContent.ITEMS.get(position).id);
            mListener.onFragmentInteraction(position);

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(int id);
    }

}
