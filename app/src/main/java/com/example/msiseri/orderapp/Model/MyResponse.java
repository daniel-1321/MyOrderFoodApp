package com.example.msiseri.orderapp.Model;

import java.util.List;

/**
 * Created by MSI SERI on 04-Jan-18.
 */

public class MyResponse {
    public long multicast_id;
    public int success;
    public int failure;
    public int canonical_ids;
    public List<Result> result;
}
