package com.richardkoster.fhictagenda.api.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by richard on 7/19/13.
 */
public class Schedule {
    public String id;
    @SerializedName("item_type")
    public String itemType;
    @SerializedName("item_id")
    public String itemId;
    @SerializedName("item_name")
    public String itemName;
    public String color;
}
