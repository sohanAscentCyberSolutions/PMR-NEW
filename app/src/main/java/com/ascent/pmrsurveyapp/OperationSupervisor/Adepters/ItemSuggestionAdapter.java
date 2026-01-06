package com.ascent.pmrsurveyapp.OperationSupervisor.Adepters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.ascent.pmrsurveyapp.OperationSupervisor.Models.PackingItemModel;
import com.ascent.pmrsurveyapp.R;

import java.util.ArrayList;
import java.util.List;

public class ItemSuggestionAdapter extends ArrayAdapter
{
    private Context context;
    private List<PackingItemModel> items;
    private List<PackingItemModel> tempItems;
    private List<PackingItemModel> suggestions;
    private  SuggestionAdapterListener listener;

    public ItemSuggestionAdapter(Context context, int resource, List<PackingItemModel> items , SuggestionAdapterListener listener)
    {
        super(context, resource, 0, items);

        this.context = context;
        this.items = items;
        this.listener = listener;
        tempItems = new ArrayList<PackingItemModel>(items);
        suggestions = new ArrayList<PackingItemModel>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if (convertView == null)
        {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_item_suggestions, parent, false);
        }

        PackingItemModel item = items.get(position);

        if (item != null)
        {
            TextView itemName = view.findViewById(R.id.itemName);
            TextView itemCategory = view.findViewById(R.id.itemCategory);

            itemName.setText("Item No : "+item.itemNo +"CR Ref. : "+item.crRef);
            itemCategory.setText("Article : "+item.article);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.placeHolderClicked(item);
                }
            });
        }

        return view;
    }

    @Override
    public Filter getFilter()
    {
        return nameFilter;
    }

    Filter nameFilter = new Filter()
    {
        @Override
        public CharSequence convertResultToString(Object resultValue)
        {
            PackingItemModel str = (PackingItemModel) resultValue;
            String name = "";
            name = str.itemNo;
            return name;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            if (constraint != null)
            {
                suggestions.clear();
                for (PackingItemModel names : tempItems)
                {
                    if (names.crRef.toLowerCase().contains(constraint.toString().toLowerCase()) || names.itemNo.toLowerCase().contains(constraint.toString().toLowerCase()))
                    {
                        suggestions.add(names);
                    }

                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            }
            else
            {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            List<PackingItemModel> filterList = (ArrayList<PackingItemModel>) results.values;
            if (results != null && results.count > 0)
            {
                clear();
                for (PackingItemModel item : filterList)
                {
                    add(item);
                    notifyDataSetChanged();
                }
            }
        }
    };

    public interface SuggestionAdapterListener {
        void placeHolderClicked(PackingItemModel model);
    }

}