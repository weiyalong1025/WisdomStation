/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.winsion.dispatch.capture.constants;

/**
 * This interface provides the constants to use when sending an Intent to Barcode Scanner.
 * These strings are effectively API and cannot be changed.
 */
public interface Intents {
    interface Scan {
        /**
         * Send this intent to open the Barcodes app in scanning mode, find a barcode, and return
         * the results.
         */
        String ACTION = "com.google.zxing.client.android.SCAN";

        /**
         * By default, sending Scan.ACTION will decode all barcodes that we understand. However it
         * may be useful to limit scanning to certain formats. Use Intent.putExtra(MODE, value) with
         * one of the values below ({@link #PRODUCT_MODE}, {@link #ONE_D_MODE}, {@link #QR_CODE_MODE}).
         * Optional.
         * <p>
         * Setting this is effectively shorthnad for setting explicit formats with {@link #SCAN_FORMATS}.
         * It is overridden by that setting.
         */
        String MODE = "SCAN_MODE";

        /**
         * Comma-separated list of formats to scan for. The values must match the names of
         * {@link com.google.zxing.BarcodeFormat}s, such as {@link com.google.zxing.BarcodeFormat#EAN_13}.
         * Example: "EAN_13,EAN_8,QR_CODE"
         * <p>
         * This overrides {@link #MODE}.
         */
        String SCAN_FORMATS = "SCAN_FORMATS";

        /**
         * @see com.google.zxing.DecodeHintType#CHARACTER_SET
         */
        String CHARACTER_SET = "CHARACTER_SET";

        /**
         * Decode only UPC and EAN barcodes. This is the right choice for shopping apps which get
         * prices, reviews, etc. for products.
         */
        String PRODUCT_MODE = "PRODUCT_MODE";

        /**
         * Decode only 1D barcodes (currently UPC, EAN, Code 39, and Code 128).
         */
        String ONE_D_MODE = "ONE_D_MODE";

        /**
         * Decode only QR codes.
         */
        String QR_CODE_MODE = "QR_CODE_MODE";

        /**
         * Decode only Data Matrix codes.
         */
        String DATA_MATRIX_MODE = "DATA_MATRIX_MODE";

        /**
         * If a barcode is found, Barcodes returns RESULT_OK to onActivityResult() of the app which
         * requested the scan via startSubActivity(). The barcodes contents can be retrieved with
         * intent.getStringExtra(RESULT). If the user presses Back, the result code will be
         * RESULT_CANCELED.
         */
        String RESULT = "SCAN_RESULT";

        /**
         * Call intent.getStringExtra(RESULT_FORMAT) to determine which barcode format was found.
         * See Contents.Format for possible values.
         */
        String RESULT_FORMAT = "SCAN_RESULT_FORMAT";

        /**
         * Setting this to false will not save scanned codes in the history.
         */
        String SAVE_HISTORY = "SAVE_HISTORY";
    }

    interface Encode {
        /**
         * Send this intent to encode a piece of data as a QR code and display it full screen, so
         * that another person can scan the barcode from your screen.
         */
        String ACTION = "com.google.zxing.client.android.ENCODE";

        /**
         * The data to encode. Use Intent.putExtra(DATA, data) where data is either a String or a
         * Bundle, depending on the type and format specified. Non-QR Code formats should
         * just use a String here. For QR Code, see Contents for details.
         */
        String DATA = "ENCODE_DATA";

        /**
         * The type of data being supplied if the format is QR Code. Use
         * Intent.putExtra(TYPE, type) with one of Contents.Type.
         */
        String TYPE = "ENCODE_TYPE";

        /**
         * The barcode format to be displayed. If this isn't specified or is blank,
         * it defaults to QR Code. Use Intent.putExtra(FORMAT, format), where
         * format is one of Contents.Format.
         */
        String FORMAT = "ENCODE_FORMAT";
    }

    interface SearchBookContents {
        /**
         * Use Google Book Search to search the contents of the book provided.
         */
        String ACTION = "com.google.zxing.client.android.SEARCH_BOOK_CONTENTS";

        /**
         * The book to search, identified by ISBN number.
         */
        String ISBN = "ISBN";

        /**
         * An optional field which is the text to search for.
         */
        String QUERY = "QUERY";
    }

    interface WifiConnect {
        /**
         * Internal intent used to trigger connection to a wi-fi network.
         */
        String ACTION = "com.google.zxing.client.android.WIFI_CONNECT";

        /**
         * The network to connect to, all the configuration provided here.
         */
        String SSID = "SSID";

        /**
         * The network to connect to, all the configuration provided here.
         */
        String TYPE = "TYPE";

        /**
         * The network to connect to, all the configuration provided here.
         */
        String PASSWORD = "PASSWORD";
    }


    interface Share {
        /**
         * Give the user a choice of items to encode as a barcode, then render it as a QR Code and
         * display onscreen for a friend to scan with their phone.
         */
        String ACTION = "com.google.zxing.client.android.SHARE";
    }
}
