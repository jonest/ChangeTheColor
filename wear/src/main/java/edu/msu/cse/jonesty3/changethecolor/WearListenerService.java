package edu.msu.cse.jonesty3.changethecolor;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class WearListenerService extends WearableListenerService {
   @Override
   public void onDataChanged( DataEventBuffer dataEvents ) {
      DataMap dataMap;
      for ( DataEvent event : dataEvents ) {
         if ( event.getType() == DataEvent.TYPE_CHANGED ) {
            dataMap = DataMapItem.fromDataItem( event.getDataItem() ).getDataMap();

            Intent dataMapIntent = new Intent();
            dataMapIntent.setAction( Intent.ACTION_SEND );
            dataMapIntent.putExtra( getString( R.string.bg_color_bundle_id ), dataMap.toBundle() );

            LocalBroadcastManager.getInstance( this ).sendBroadcast( dataMapIntent );
         }
      }
   }
}
