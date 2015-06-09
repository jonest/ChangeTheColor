package edu.msu.cse.jonesty3.changethecolor;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

   GoogleApiClient _googleApiClient;

   @Override
   protected void onCreate( Bundle savedInstanceState ) {
      super.onCreate( savedInstanceState );
      setContentView( R.layout.activity_main );
      _googleApiClient = new GoogleApiClient.Builder( this )
              .addApi( Wearable.API )
              .addConnectionCallbacks( this )
              .addOnConnectionFailedListener( this )
              .build();
   }

   @Override
   protected void onStart() {
      super.onStart();
      _googleApiClient.connect();
   }

   @Override
   protected void onStop() {
      if ( _googleApiClient != null && _googleApiClient.isConnected() ) {
         _googleApiClient.disconnect();
      }
      super.onStop();
   }

   public void buttonPush( View view ) {
      ColorDrawable buttonColorDrawable = (ColorDrawable) findViewById( view.getId() ).getBackground();

      DataMap dataMap = new DataMap();
      dataMap.putInt( getString( R.string.bg_color_int_id ), buttonColorDrawable.getColor() );

      new SendToDataLayerThread( getString( R.string.wearable_data_path ), dataMap ).start();
   }

   @Override
   public void onConnected( Bundle bundle ) {
   }

   @Override
   public void onConnectionSuspended( int i ) {

   }

   @Override
   public void onConnectionFailed( ConnectionResult connectionResult ) {

   }

   class SendToDataLayerThread extends Thread {
      String _path;
      DataMap _dataMap;

      public SendToDataLayerThread( String path, DataMap dataMap ) {
         _dataMap = dataMap;
         _path = path;
      }

      public void run() {
         PutDataMapRequest putDataMapRequest = PutDataMapRequest.create( _path );
         putDataMapRequest.getDataMap().putAll( _dataMap );
         PutDataRequest request = putDataMapRequest.asPutDataRequest();
         Wearable.DataApi.putDataItem( _googleApiClient, request ).await();
      }
   }
}
