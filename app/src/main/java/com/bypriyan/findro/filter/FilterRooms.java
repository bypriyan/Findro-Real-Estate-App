package com.bypriyan.findro.filter;

import android.widget.Filter;

import com.bypriyan.findro.adapter.AdapterRoomsVertical;
import com.bypriyan.findro.model.ModelRooms;

import java.util.ArrayList;

public class FilterRooms extends Filter {

    private AdapterRoomsVertical adapter;
    private ArrayList<ModelRooms> filterList;

    public FilterRooms(AdapterRoomsVertical adapter, ArrayList<ModelRooms> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if(charSequence != null && charSequence.length()>0){
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelRooms> filteredModel = new ArrayList<>();
            for(int i=0; i<filterList.size(); i++){
                if(filterList.get(i).getCity().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getState().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getMonthlyRent().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getAddress().toUpperCase().contains(charSequence)||
                        filterList.get(i).getPropertyType().toUpperCase().contains(charSequence) ){

                    filteredModel.add(filterList.get(i));
                }
            }
            results.count = filteredModel.size();
            results.values = filteredModel;

        }else{
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
    adapter.modelRoomsArrayList = (ArrayList<ModelRooms>)results.values;
    adapter.notifyDataSetChanged();
    }
}
