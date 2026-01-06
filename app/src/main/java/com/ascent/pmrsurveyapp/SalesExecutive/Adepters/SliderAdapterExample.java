package com.ascent.pmrsurveyapp.SalesExecutive.Adepters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.ascent.pmrsurveyapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

//public class SliderAdapterExample extends
//        SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {
//
//    private Context context;
//    private List<String> mSliderItems = new ArrayList<>();
//
//    public SliderAdapterExample(Context context) {
//        this.context = context;
//    }
//
//    public void renewItems(List<String> sliderItems) {
//        this.mSliderItems = sliderItems;
//        notifyDataSetChanged();
//    }
//
//    public void deleteItem(int position) {
//        this.mSliderItems.remove(position);
//        notifyDataSetChanged();
//    }
//
//    public void addItem(String sliderItem) {
//        this.mSliderItems.add(sliderItem);
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
//        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, null);
//        return new SliderAdapterVH(inflate);
//    }
//
//    @Override
//    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
//
//        String sliderItem = mSliderItems.get(position);
//
//        Picasso.with(context).load(sliderItem).into(viewHolder.iv_auto_image_slider);
//
//
//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    @Override
//    public int getCount() {
//        //slider view count could be dynamic size
//        return mSliderItems.size();
//    }
//
//    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
//
//        View itemView;
//        ImageView iv_auto_image_slider;
//
//        public SliderAdapterVH(View itemView) {
//            super(itemView);
//            iv_auto_image_slider = itemView.findViewById(R.id.iv_auto_image_slider);
//
//            this.itemView = itemView;
//        }
//    }
//
//}
