package com.wish.order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {

    private static final String TAG = "zxp_SQL";
	/** Called when the activity is first created. */
    
	private Button addBtn;
	private Button modiBtn;
	private Button cancelBtn;
	private Button queryBtn;
	private Button genDBTestBtn;
	private myButtonClickListener btnClickListener = new myButtonClickListener();
	private TextView querySum;
	private EditText mmid;
	private EditText article;
	private EditText price;
	private EditText quantity;
	private EditText date;
	private EditText mark;
	private EditText queryinfo;
	private ListView listview;
	private CheckBox cbOrdered;
	private CheckBox cbShipped;
   
	/*SQL Database object */  
	private SQLiteDatabase      mSQLiteDatabase = null;
	private final static String DATABASE_NAME   = "mydb.db";  
	private final static String TABLE_NAME_BUYIN      = "BuyIn";
	private final static String TABLE_NAME_SELLOUT    = "SellOut";
	private Cursor cursor; 
	private final String [] from = {MMID, ARTICLE, PRICE, QUANTITY, SUBSUM, DATE, ORDERED, SHIP, MARK};
	private final int []to = {R.id.mmid, R.id.article, R.id.price, R.id.quantity, R.id.subSum, R.id.date, 
			R.id.ordered, R.id.shipped, R.id.mark};
	/* Column in the table SellOut*/ 
	private final static String NO  	= "_id";  
	private final static String MMID  	= "Name";  
	private final static String ARTICLE = "Article";  
	private final static String PRICE   = "Price";  
	private final static String QUANTITY = "Quantity";  
	private final static String SUBSUM 	= "SubSum"; 
	private final static String DATE 	= "Date"; 
	private final static String ORDERED	= "Ordered"; 
	private final static String SHIP 	= "Ship"; 
	private final static String MARK 	= "Mark";  
	
	private int modifying_id = -1;
	private SimpleCursorAdapter adapter;
	  
	//Create SQL TABLE 
	private final static String CREATE_TABLE_SELLOUT 
	= "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_SELLOUT + " ("+NO+" INTEGER PRIMARY KEY AUTOINCREMENT,"+MMID+" TEXT,"
	+ARTICLE+" TEXT,"+PRICE+" INTEGER,"+QUANTITY+" INTEGER,"+SUBSUM+" INTEGER,"+DATE+" TEXT,"
	+ORDERED+" TEXT,"+SHIP+" TEXT,"+MARK+" TEXT)"; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        addBtn = (Button)findViewById(R.id.addBtn);
        modiBtn = (Button)findViewById(R.id.modiBtn);
        queryBtn = (Button)findViewById(R.id.queryBtn);
        genDBTestBtn = (Button)findViewById(R.id.genDBTest);
        cancelBtn = (Button)findViewById(R.id.cancelBtn);
        addBtn.setOnClickListener(btnClickListener); 
        modiBtn.setOnClickListener(btnClickListener); 
        queryBtn.setOnClickListener(btnClickListener); 
        genDBTestBtn.setOnClickListener(btnClickListener); 
        cancelBtn.setOnClickListener(btnClickListener); 
  
        addBtn.setOnTouchListener(btnClickListener); 
        modiBtn.setOnTouchListener(btnClickListener); 
        queryBtn.setOnTouchListener(btnClickListener); 
        genDBTestBtn.setOnTouchListener(btnClickListener); 
        cancelBtn.setOnTouchListener(btnClickListener); 
        
        listview = (ListView)findViewById(R.id.queryResult);
        mmid = (EditText)findViewById(R.id.mmid);
        article = (EditText)findViewById(R.id.article);
        price = (EditText)findViewById(R.id.price);
        quantity = (EditText)findViewById(R.id.quantity);
        date = (EditText)findViewById(R.id.date);
        mark = (EditText)findViewById(R.id.mark);
        queryinfo = (EditText)findViewById(R.id.queryInput);
        
        querySum = (TextView)findViewById(R.id.querySum);
        cbOrdered = (CheckBox)findViewById(R.id.checkBoxOrdered);
        cbShipped = (CheckBox)findViewById(R.id.checkBoxShipped);
        
        
        mSQLiteDatabase = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null); 
        try  
        {  
            mSQLiteDatabase.execSQL(CREATE_TABLE_SELLOUT);  
        }
        catch (SQLException sqlex)  
        {  
        	sqlex.printStackTrace();
        }  
		
        onListViewItemSingleClickHandler();
        onListViewItemLongPressHandler();
   	
		adapter = new SimpleCursorAdapter(this, R.layout.sqlite_query_listview, cursor, from, to);
		listview.setAdapter(adapter);
		
		Log.d(TAG, "OnCreate Finished!");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	class myButtonClickListener implements OnClickListener, OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (v.getId()) {			
			case R.id.addBtn:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					addBtn.setBackgroundColor(getResources().getColor(R.color.darkorange));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                	addBtn.setBackgroundColor(getResources().getColor(R.color.gainsboro));
                }
				break;
				
			case R.id.modiBtn:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					modiBtn.setBackgroundColor(getResources().getColor(R.color.darkorange));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                	modiBtn.setBackgroundColor(getResources().getColor(R.color.gainsboro));
                }
				break;

			case R.id.cancelBtn:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					cancelBtn.setBackgroundColor(getResources().getColor(R.color.darkorange));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                	cancelBtn.setBackgroundColor(getResources().getColor(R.color.gainsboro));
                }
				break;
				
			case R.id.genDBTest:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					genDBTestBtn.setBackgroundColor(getResources().getColor(R.color.darkorange));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                	genDBTestBtn.setBackgroundColor(getResources().getColor(R.color.gainsboro));
                }
				break;
				
			case R.id.queryBtn:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					queryBtn.setBackgroundColor(getResources().getColor(R.color.darkorange));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                	queryBtn.setBackgroundColor(getResources().getColor(R.color.gainsboro));
                }
				break;
			}
			return false;
		}		
		@Override
		public void onClick(View view) {			
			switch (view.getId()) {
	
			case R.id.addBtn:
				addItem();
				queryInfo(queryinfo.getText().toString());
				break;
				
			case R.id.modiBtn:
				updateItem(modifying_id);
				queryInfo(queryinfo.getText().toString());
				modiBtn.setVisibility(View.GONE);
				cancelBtn.setVisibility(View.GONE);
				addBtn.setVisibility(View.VISIBLE);
				break;

			case R.id.cancelBtn:
				modiBtn.setVisibility(View.GONE);
				cancelBtn.setVisibility(View.GONE);
				addBtn.setVisibility(View.VISIBLE);
				break;
				
			case R.id.genDBTest:
				mSQLiteDatabase.execSQL("DROP table if exists " + TABLE_NAME_SELLOUT);
				mSQLiteDatabase.execSQL(CREATE_TABLE_SELLOUT);  
				genDatabase();
				break;
				
			case R.id.queryBtn:
				queryInfo(queryinfo.getText().toString());
				break;
			}
		}
	}

	protected void addItem() {			
		ContentValues cv = new ContentValues(); 
	    String str;
	    int t_price = 0, t_quantity, subSum;
	    
	    str = mmid.getText().toString();
	    if (str.equals("")){
	    	Toast.makeText(getApplicationContext(), getString(R.string.sql_hint)+getString(R.string.sql_mmid), Toast.LENGTH_SHORT).show();
	    	return;
	    }
	    cv.put(MMID, str); 
	    
	    str = article.getText().toString();
	    if (str.equals("")){
	    	Toast.makeText(getApplicationContext(), getString(R.string.sql_hint)+getString(R.string.sql_article), Toast.LENGTH_SHORT).show();
	    	return;
	    }
	    cv.put(ARTICLE, str); 
	    
	    str = price.getText().toString();
	    try{
	    	t_price = Integer.parseInt(str);
	    	}catch (NumberFormatException e){
	    		Toast.makeText(getApplicationContext(), getString(R.string.sql_hint)+getString(R.string.sql_price), Toast.LENGTH_SHORT).show();
		    	return;
	    }
	    cv.put(PRICE, t_price); 
	    
	    str = quantity.getText().toString();
	    try{
	    	t_quantity = Integer.parseInt(str);
	    	}catch (NumberFormatException e){
	    		Toast.makeText(getApplicationContext(), getString(R.string.sql_hint)+getString(R.string.sql_quantity), Toast.LENGTH_SHORT).show();
		    	return;
	    }
	    cv.put(QUANTITY, t_quantity);
	
    	subSum = t_price * t_quantity;
    	if(subSum == 0){
    		Toast.makeText(getApplicationContext(), getString(R.string.sql_input_err), Toast.LENGTH_SHORT).show();
    		return;
    	}
    	cv.put(SUBSUM, subSum);
    	
	    str = date.getText().toString();
	    if (str.equals("")){
	    	SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMdd");     
	    	Date curDate = new Date(System.currentTimeMillis());//get current time    
	    	str = formatter.format(curDate);  
	    	Log.d(TAG, str);
	    }
	    cv.put(DATE, str); 

	    if(cbOrdered.isChecked()) 
	    	cv.put(ORDERED, getString(R.string.sql_ordered));
	    else 
	    	cv.put(ORDERED, getString(R.string.sql_not_ordered));

	    if(cbShipped.isChecked()) 
	    	cv.put(SHIP, getString(R.string.sql_shipped));
	    else 
	    	cv.put(SHIP, getString(R.string.sql_not_shipped));
	    
	    str = mark.getText().toString();
	    cv.put(MARK, str);  
	    
	    //insert an Item record
	    mSQLiteDatabase.insert(TABLE_NAME_SELLOUT, null, cv); 
	    Toast.makeText(getApplicationContext(), "Add Success", Toast.LENGTH_SHORT).show();
	

	    article.getText().clear();
	    //mmid.getText().clear();
	    //price.getText().clear();
	    //quantity.getText().clear();
	    //date.getText().clear();
	    mark.getText().clear();
	}

	protected void delItem(int id){
		//mSQLiteDatabase.delete(TABLE_NAME, NO+" = ?", new String(Integer.toString(id)));
		mSQLiteDatabase.execSQL("DELETE FROM " + TABLE_NAME_SELLOUT + " WHERE _id is " + id );
	}
	@SuppressLint("SimpleDateFormat")
	protected void updateItem(int id) {			
		ContentValues cv = new ContentValues(); 
	    String str;
	    int t_price = 0, t_quantity, subSum;
	    
	    str = mmid.getText().toString();
	    if (str.equals("")){
	    	Toast.makeText(getApplicationContext(), getString(R.string.sql_hint)+getString(R.string.sql_mmid), Toast.LENGTH_SHORT).show();
	    	return;
	    }
	    cv.put(MMID, str); 
	    
	    str = article.getText().toString();
	    if (str.equals("")){
	    	Toast.makeText(getApplicationContext(), getString(R.string.sql_hint)+getString(R.string.sql_article), Toast.LENGTH_SHORT).show();
	    	return;
	    }
	    cv.put(ARTICLE, str); 
	    
	    str = price.getText().toString();
	    try{
	    	t_price = Integer.parseInt(str);
	    	}catch (NumberFormatException e){
	    		Toast.makeText(getApplicationContext(), getString(R.string.sql_hint)+getString(R.string.sql_price), Toast.LENGTH_SHORT).show();
		    	return;
	    }
	    cv.put(PRICE, t_price); 
	    
	    str = quantity.getText().toString();
	    try{
	    	t_quantity = Integer.parseInt(str);
	    	}catch (NumberFormatException e){
	    		Toast.makeText(getApplicationContext(), getString(R.string.sql_hint)+getString(R.string.sql_quantity), Toast.LENGTH_SHORT).show();
		    	return;
	    }
	    cv.put(QUANTITY, t_quantity);
	
    	subSum = t_price * t_quantity;
    	if(subSum == 0){
    		Toast.makeText(getApplicationContext(), getString(R.string.sql_input_err), Toast.LENGTH_SHORT).show();
    		return;
    	}
    	cv.put(SUBSUM, subSum);
    	
	    str = date.getText().toString();
	    if (str.equals("")){
	    	SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMdd");     
	    	Date curDate = new Date(System.currentTimeMillis());//get current time
	    	str = formatter.format(curDate);  
	    	Log.d(TAG, str);
	    }
	    cv.put(DATE, str); 
	    
	    if(cbOrdered.isChecked()) 
	    	cv.put(ORDERED, getString(R.string.sql_ordered));
	    else 
	    	cv.put(ORDERED, getString(R.string.sql_not_ordered));

	    if(cbShipped.isChecked()) 
	    	cv.put(SHIP, getString(R.string.sql_shipped));
	    else 
	    	cv.put(SHIP, getString(R.string.sql_not_shipped));
	    
	    str = mark.getText().toString();
	    cv.put(MARK, str);  
	    
	    //update this item record
	    Log.d(TAG, "id=" + id);
	    mSQLiteDatabase.update(TABLE_NAME_SELLOUT, cv, NO + " = " +id , null); 
	    Toast.makeText(getApplicationContext(), "Modi Success", Toast.LENGTH_SHORT).show();
	
	    mmid.getText().clear();
	    article.getText().clear();
	    price.getText().clear();
	    quantity.getText().clear();
	    date.getText().clear();
	    mark.getText().clear();
	}
	
	@SuppressLint("SimpleDateFormat")
	protected void genDatabase(){
		ProgressDialog mDialog = null;
		ContentValues cv = new ContentValues(); 
	    int price = 0, quantity, subSum, index;
	    String str;
	    mDialog = ProgressDialog.show(this, "Gen Database", "Generating Database, please wait", true);  
	    
	    Random ran = new Random(200000);
	    
		for(index=0; index<200; index++){
		    cv.put(MMID, "Name_" + ran.nextInt(80)); 
		    
		    str = "PRO_"+ran.nextInt(100);
		    cv.put(ARTICLE, str); 
		    
		    price = ran.nextInt(300)+12;
		    cv.put(PRICE, price); 
		    
		    quantity = ran.nextInt(3)+1;
		    cv.put(QUANTITY, quantity);
		
	    	subSum = price * quantity;
	    	if(subSum == 0){
	    		Toast.makeText(getApplicationContext(), "Auto gen error!", Toast.LENGTH_SHORT).show();
	    		continue;
	    	}
	    	cv.put(SUBSUM, subSum);
	    	
	    	int i = ran.nextInt(14);
		    cv.put(DATE, DateString[i]); 
		    
		    if(cbOrdered.isChecked()) 
		    	cv.put(ORDERED, getString(R.string.sql_ordered));
		    else 
		    	cv.put(ORDERED, getString(R.string.sql_not_ordered));

		    if(cbShipped.isChecked()) 
		    	cv.put(SHIP, getString(R.string.sql_shipped));
		    else 
		    	cv.put(SHIP, getString(R.string.sql_not_shipped));
		    
		    boolean tmp = ran.nextBoolean();
	    	if(tmp) str = "Here may show Address";
	    	else str = "Other comments";
		    cv.put(MARK, str);  
		    
		    //Insert an item
		    try{
		    	mSQLiteDatabase.insert(TABLE_NAME_SELLOUT, null, cv); 
		    }catch (SQLiteException e){
		    	Toast.makeText(getApplicationContext(), "Insert one error", Toast.LENGTH_SHORT).show();
		    	return;
		    }
		}
		mDialog.dismiss();
	    Toast.makeText(getApplicationContext(), getString(R.string.sql_succ), Toast.LENGTH_SHORT).show();
	}
	protected void queryInfo(String str){
		int query_sum = 0;
		if(cursor !=null) cursor.close();
		if(str.equals("")){
			Toast.makeText(getApplicationContext(), "Query All", Toast.LENGTH_SHORT).show();
			cursor = mSQLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME_SELLOUT + " ORDER BY "+DATE+" DESC", null);
		}else if (str.startsWith("2014") || str.startsWith("2015")){
			Toast.makeText(getApplicationContext(), "Query by time", Toast.LENGTH_SHORT).show();
			cursor = mSQLiteDatabase.rawQuery("SELECT * FROM "+ TABLE_NAME_SELLOUT +" WHERE " + DATE +" LIKE ?"+ " ORDER BY "+DATE+" DESC", 
												new String[]{new String(str +"%%")});  
		}else {
			Toast.makeText(getApplicationContext(), "Query by "+MMID, Toast.LENGTH_SHORT).show();
			cursor = mSQLiteDatabase.rawQuery("SELECT * FROM "+ TABLE_NAME_SELLOUT +" WHERE " + MMID +" LIKE ?"+ " ORDER BY "+DATE+" DESC", 
					new String[]{new String("%%"+ str +"%%")});  
		}

		while (cursor.moveToNext()) {  
			query_sum += cursor.getInt(cursor.getColumnIndex(SUBSUM));
        } 
		String reslut = getString(R.string.sql_query_sum) + query_sum;
		querySum.setText(reslut);
		adapter = new SimpleCursorAdapter(this, R.layout.sqlite_query_listview, cursor, from, to);
		listview.setAdapter(adapter);
	}	

	protected void onListViewItemSingleClickHandler(){		
    	listview.setOnItemClickListener(new OnItemClickListener(){
    		@Override
    		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    			cursor.moveToPosition(arg2);
    			mmid.setText(cursor.getString(cursor.getColumnIndex(MMID)));
    			article.setText(cursor.getString(cursor.getColumnIndex(ARTICLE)));
    			price.setText(cursor.getString(cursor.getColumnIndex(PRICE)));
    			quantity.setText(cursor.getString(cursor.getColumnIndex(QUANTITY)));
    			date.setText(cursor.getString(cursor.getColumnIndex(DATE)));
    			
    			cbOrdered.setChecked(cursor.getString(cursor.getColumnIndex(ORDERED)).equals(getString(R.string.sql_ordered)));
    			cbShipped.setChecked(cursor.getString(cursor.getColumnIndex(SHIP)).equals(getString(R.string.sql_shipped)));
    			
    			mark.setText(cursor.getString(cursor.getColumnIndex(MARK)));
    			
    			addBtn.setVisibility(View.VISIBLE);
    			modiBtn.setVisibility(View.GONE);
    			cancelBtn.setVisibility(View.GONE);
    		}			
    	});
	}
	
	protected void onListViewItemLongPressHandler(){ 
		//setOnCreateContextMenuListener use together with onContextItemSelected 
		listview.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
				//menu.setHeaderTitle("ZHU");
				menu.add(0, 0, 0, R.string.sql_modi_one);
				menu.add(0, 1, 0, R.string.sql_del_one); 
                menu.add(0, 2, 0, R.string.sql_del_all); 
                menu.add(0, 3, 0, R.string.sql_cancel); 
			}
		}); 
	}
	//process long presses
	public boolean onContextItemSelected(MenuItem item) {
	     AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo(); 
	     //info.id is equal _id in SQL table, while info.position is equal to Adapter index.
	     Log.d(TAG, "id=" + info.id + ".  position = " + info.position);
	     switch (item.getItemId()) { 
	     case 0: 
	    	 Toast.makeText(getApplicationContext(), getString(R.string.sql_modi_one), Toast.LENGTH_SHORT).show(); 
	    	 cursor.moveToPosition(info.position);
	    	 modifying_id = (int) info.id;
	    	 addBtn.setVisibility(View.GONE);
	    	 modiBtn.setVisibility(View.VISIBLE);
	    	 cancelBtn.setVisibility(View.VISIBLE);
	    	 mmid.setText(cursor.getString(cursor.getColumnIndex(MMID)));
	    	 article.setText(cursor.getString(cursor.getColumnIndex(ARTICLE)));
	    	 price.setText(cursor.getString(cursor.getColumnIndex(PRICE)));
	    	 quantity.setText(cursor.getString(cursor.getColumnIndex(QUANTITY)));
	    	 date.setText(cursor.getString(cursor.getColumnIndex(DATE)));
	    	 cbOrdered.setChecked(cursor.getString(cursor.getColumnIndex(ORDERED)).equals(getString(R.string.sql_ordered)));
 			 cbShipped.setChecked(cursor.getString(cursor.getColumnIndex(SHIP)).equals(getString(R.string.sql_shipped)));
	    	 mark.setText(cursor.getString(cursor.getColumnIndex(MARK)));
             break;
	     case 1: 
             Toast.makeText(getApplicationContext(), getString(R.string.sql_del_one), Toast.LENGTH_SHORT).show(); 
             delItem((int) (info.id));
             queryInfo(queryinfo.getText().toString());
             break;
	     case 2: 
	    	 Toast.makeText(getApplicationContext(), getString(R.string.sql_del_all), Toast.LENGTH_SHORT).show(); 
	    	 cursor.moveToFirst();
	    	do{  
	    		 delItem( cursor.getInt(cursor.getColumnIndex(NO)));
	    	 }while (cursor.moveToNext());
	    	 queryInfo(queryinfo.getText().toString());
             break;
	     case 3: 
	    	 Toast.makeText(getApplicationContext(), getString(R.string.sql_cancel), Toast.LENGTH_SHORT).show(); 
             break;
	     default: 
             break; 
	     }
	     return super.onContextItemSelected(item);
	}
	/* another way to implement LongClick menu
	listview.setOnItemLongClickListener(new OnItemLongClickListener() {  
        @Override  
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1,  int arg2, long arg3) {  
            // TODO Auto-generated method stub  
            Log.d(TAG, "index = " + arg2);  
            return false;  
        }  
    });  
    */	
	@Override
	protected void onStart() {
        mSQLiteDatabase = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null); 
        try  
        {  
            mSQLiteDatabase.execSQL(CREATE_TABLE_SELLOUT);  
        }
        catch (SQLException sqlex)  
        {  
        	sqlex.printStackTrace();
        }  
		super.onStart();
	}

	@Override
	protected void onStop() {
		if(mSQLiteDatabase != null) mSQLiteDatabase.close();
		if(cursor != null) cursor.close();
		super.onStop();
	}
		
	String DateString[] = { "20141010", "20141012", "20141013", "20141014", "20141015", "20141017", "20141020",
			"20151110", "20151212", "20151113", "20151014", "20151215", "20141117", "20141222",
	};
	
}
