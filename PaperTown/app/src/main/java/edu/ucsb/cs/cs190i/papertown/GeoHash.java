/*
 *  Copyright (c) 2017 - present, Zhenyu Yang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 */

package edu.ucsb.cs.cs190i.papertown;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhenyu on 2017-05-19.
 */

public class GeoHash {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static List<String> genAllGeoHash(double neLat, double swLat, double neLng, double swLng){
        List<String> res = new ArrayList<>();

        int neLa = (int) (neLat * 10);
        int neLn = (int) (neLng * 10);
        int swLa = (int) (swLat * 10);
        int swLn = (int) (swLng * 10);

        for (int i = swLa; i <= neLa; i++){
            for (int j = swLn; j <= neLn; j++){
                res.add(genGeoHash(i, j));
            }
        }

        return res;
    }

    public static String genGeoHash(int lat, int lng){
        String geoHash = "{ lat: " + lat + ", { lng: " + lng + "}";
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance( "SHA-1" );
            byte[] bytes = geoHash.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();

            // This is ~55x faster than looping and String.formating()
            hash = bytesToHex( bytes );
        }
        catch( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }
        catch( UnsupportedEncodingException e )
        {
            e.printStackTrace();
        }
        return hash;
    }

    public static String genGeoHash(double lat, double lng){
        String geoHash = "{ lat: " + (int)(lat * 10) + ", { lng: " + (int)(lng * 10) + "}";
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance( "SHA-1" );
            byte[] bytes = geoHash.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();

            // This is ~55x faster than looping and String.formating()
            hash = bytesToHex( bytes );
        }
        catch( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }
        catch( UnsupportedEncodingException e )
        {
            e.printStackTrace();
        }
        return hash;
    }

    public static String bytesToHex( byte[] bytes )
    {
        char[] hexChars = new char[ bytes.length * 2 ];
        for( int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[ j ] & 0xFF;
            hexChars[ j * 2 ] = hexArray[ v >>> 4 ];
            hexChars[ j * 2 + 1 ] = hexArray[ v & 0x0F ];
        }
        return new String( hexChars );
    }
}