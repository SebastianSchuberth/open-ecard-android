/****************************************************************************
 * Copyright (C) 2017 ecsec GmbH.
 * All rights reserved.
 * Contact: ecsec GmbH (info@ecsec.de)
 *
 * This file is part of the Open eCard App.
 *
 * GNU General Public License Usage
 * This file may be used under the terms of the GNU General Public
 * License version 3.0 as published by the Free Software Foundation
 * and appearing in the file LICENSE.GPL included in the packaging of
 * this file. Please review the following information to ensure the
 * GNU General Public License version 3.0 requirements will be met:
 * http://www.gnu.org/copyleft/gpl.html.
 *
 * Other Usage
 * Alternatively, this file may be used in accordance with the terms
 * and conditions contained in a signed written agreement between
 * you and ecsec GmbH.
 *
 ***************************************************************************/

package org.openecard.demo.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.openecard.android.system.ServiceResponseStatusCodes;
import org.openecard.android.system.OpeneCardContext;
import org.openecard.android.system.OpeneCardServiceHandler;
import org.openecard.android.system.OpeneCardServiceClientHandler;
import org.openecard.common.util.TR03112Utils;
import org.openecard.demo.R;
import org.openecard.android.system.ServiceErrorResponse;
import org.openecard.android.system.ServiceWarningResponse;
import org.openecard.android.utils.NfcUtils;


/**
 * Activity providing the functionality to initialize and destroy the Open eCard Stack.
 * Once the initialization is complete,
 *
 * @author Mike Prechtl
 * @author Tobias Wich
 */
public class MainActivity extends Activity {

	private OpeneCardServiceClientHandler serviceClient;

	private TextView txtView;
	private Button startBtn;
	private Button stopBtn;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// enable developer mode if needed
		TR03112Utils.DEVELOPER_MODE = false;

		// initialize connection to Open eCard App
		serviceClient = new OpeneCardServiceClientHandler(this, new InitServiceHandler());

		setContentView(R.layout.activity_main);

		// set up gui components
		txtView = findViewById(R.id.textView2);
		txtView.setVisibility(View.INVISIBLE);

		startBtn = findViewById(R.id.btnStart);
		startBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startBtn.setEnabled(false);
				// start Open eCard Stack
				serviceClient.startService();
			}
		});

		stopBtn = findViewById(R.id.btnStop);
		stopBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				stopBtn.setEnabled(false);
				// stop Open eCard Stack
				serviceClient.stopService();
			}
		});
	}

	@Override
	protected void onDestroy() {
		// stop Open eCard Stack when this activity is destroyed
		if (serviceClient.isInitialized()) {
			serviceClient.stopService();
		}
		super.onDestroy();
	}



	///
	/// Handler functions for the initialization or termination of the Open eCard Stack management Android Service
	///

	private class InitServiceHandler implements OpeneCardServiceHandler {

		@Override
		public void onConnectionSuccess(OpeneCardContext ctx) {
			stopBtn.setEnabled(true);

			Intent i = new Intent(getApplicationContext(), IdsActivity.class);
			if(getIntent().getData() != null){
				i.setData(getIntent().getData());
			}

			startActivity(i);
		}

		@Override
		public void onConnectionFailure(ServiceErrorResponse serviceErrorResponse) {
			startBtn.setEnabled(true);
			txtView.setText(serviceErrorResponse.getMessage());
			txtView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onConnectionFailure(ServiceWarningResponse serviceWarningResponse) {
			startBtn.setEnabled(true);
			if (serviceWarningResponse.getStatusCode() == ServiceResponseStatusCodes.NFC_NOT_ENABLED) {
				// maybe go to nfc settings
				NfcUtils.getInstance().goToNFCSettings(MainActivity.this);
			}
		}

		@Override
		public void onDisconnectionSuccess() {
			startBtn.setEnabled(true);
		}

		@Override
		public void onDisconnectionFailure(ServiceErrorResponse serviceErrorResponse) {

		}

		@Override
		public void onDisconnectionFailure(ServiceWarningResponse serviceWarningResponse) {

		}

	}

}
