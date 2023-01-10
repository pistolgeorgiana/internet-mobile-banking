package eu.ase.ro.dam.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import eu.ase.ro.dam.R;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> expandableList;
    private HashMap<String, List<String>> expandableListContent;

    public ExpandableListAdapter(Context context) {
        this.context = context;
        this.expandableListContent = getDetails();
        this.expandableList = new ArrayList<String>(expandableListContent.keySet());
    }

    public HashMap<String, List<String>> getDetails() {
        HashMap<String, List<String>> details =
                new HashMap<String, List<String>>();
        List<String> charges = Arrays.asList(context.getResources()
                .getStringArray(R.array.profile_charges));
        List<String> interests = Arrays.asList(context.getResources()
                .getStringArray(R.array.profile_interests));
        details.put("Charges", charges);
        details.put("Interests", interests);
        return details;
    }

    @Override
    public int getGroupCount() {
        return this.expandableList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.expandableListContent
                .get(this.expandableList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.expandableList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.expandableListContent
                .get(this.expandableList.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String title = (String) getGroup(groupPosition);
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitle = (TextView) convertView.findViewById(R.id.profile_tv_list_title);
        listTitle.setTypeface(null, Typeface.BOLD_ITALIC);
        listTitle.setText(title);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childTitle = (String) getChild(groupPosition, childPosition);
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView listDetails = (TextView) convertView.findViewById(R.id.profile_tv_details);
        listDetails.setText(childTitle);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
