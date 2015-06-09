package edu.msu.cse.jonesty3.changethecolor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.HapticFeedbackConstants;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
   GoogleApiClient _googleApiClient;
   private final ResultCallback<DataItemBuffer> _resultCallback = new ResultCallback<DataItemBuffer>() {
      @Override
      public void onResult( DataItemBuffer dataItems ) {
         if ( dataItems.getCount() != 0 ) {
            DataMap dataMap = DataMapItem.fromDataItem( dataItems.get( 0 ) ).getDataMap();

            int backgroundColor = dataMap.getInt( getString( R.string.bg_color_int_id ) );

            View watchView = findViewById( android.R.id.content );
            watchView.setBackgroundColor( backgroundColor );
            watchView.invalidate();
         }

         dataItems.release();
      }
   };

   @Override
   protected void onCreate( Bundle savedInstanceState ) {
      super.onCreate( savedInstanceState );

      setContentView( R.layout.activity_main );

      IntentFilter messageFilter = new IntentFilter( Intent.ACTION_SEND );
      WearMessageReceiver messageReceiver = new WearMessageReceiver();
      LocalBroadcastManager.getInstance( this ).registerReceiver( messageReceiver, messageFilter );

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

   @Override
   public void onConnected( Bundle bundle ) {
      Wearable.DataApi.getDataItems( _googleApiClient ).setResultCallback( _resultCallback );
   }

   @Override
   public void onConnectionSuspended( int i ) {

   }

   @Override
   public void onConnectionFailed( ConnectionResult connectionResult ) {

   }

   @Override
   public void onDestroy() {
      _googleApiClient.disconnect();
      super.onDestroy();
   }

   class WearMessageReceiver extends BroadcastReceiver {
      @Override
      public void onReceive( Context context, Intent intent ) {
         Bundle backgroundColorBundle = intent.getBundleExtra( getString( R.string.bg_color_bundle_id ) );
         int backgroundColor = backgroundColorBundle.getInt( getString( R.string.bg_color_int_id ) );

         View watchView = findViewById( android.R.id.content );
         watchView.setBackgroundColor( backgroundColor );
         watchView.performHapticFeedback( HapticFeedbackConstants.VIRTUAL_KEY );
         watchView.invalidate();
      }
   }
}
