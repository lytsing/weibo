/*
 * Copyright (C) 2010 lytsing.org
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

package org.lytsing.android.weibo.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.lytsing.android.weibo.R;
import org.lytsing.android.weibo.util.Util;

/**
 * abstract SectionAdapter.
 *
 */
public abstract class SectionAdapter extends BaseAdapter {
    protected int mCount;
    protected boolean mDeactivated;
    protected View mSectionHeaderView;

    /**
     * Constructor.
     *
     * @param sectionTitleId The resource ID for a layout file containing a layout to use when
     *                  instantiating views.
     * @param context The current context.
     * @param parent The parent that this view will eventually be attached to
     */
    public SectionAdapter(int sectionTitleId, Context context, ViewGroup parent) {
        this(Util.inflateView(R.layout.asset_info_section_header, context, parent));

        ((TextView) mSectionHeaderView).setText(sectionTitleId);
    }

    /**
     * Constructor.
     *
     * @param sectionHeaderView The header view
     */
    public SectionAdapter(View sectionHeaderView) {
        mSectionHeaderView = sectionHeaderView;
        mDeactivated = false;
        mCount = 0;
    }

    public void activate() {
        if (mDeactivated) {
            mDeactivated = false;
            notifyDataSetChanged();
        }
    }

    /**
     * Are all items in this SectionAdapter enable?
     * If yes it means all items are selectable and clickable.
     */
    public boolean areAllItemsEnabled() {
        return false;
    }

    public void deactivate() {
        if (!mDeactivated) {
            mDeactivated = true;
            notifyDataSetChanged();
        }
    }

    /**
     * How many items are in the data set represented by this
     * Adapter.
     */
    public int getCount() {
        if (mDeactivated) {
            return 0;
        } else {
            return mCount + 1; // add one for header
        }
    }

    /**
     * Get the data item associated with the specified
     * position in the data set.
     *
     * @param position Position of the item whose data we want
     */
    public Object getItem(int position) {
        if (position == 0) {
            return mSectionHeaderView;
        }

        return null;
    }

    /**
     * Get the row id associated with the specified position
     * in the list.
     *
     * @param position Position of the item whose data we want
     */
    public long getItemId(int position) {
        if (position == 0) {
            return mSectionHeaderView.getId();
        }

        return 0;
    }

    /**
     * Get a View that displays the data at the specified
     * position in the data set.
     *
     * @param position Position of the item whose data we want
     * @param convertView View to recycle, if not null
     * @param parent ViewGroup containing the returned View
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        return position == 0 ? mSectionHeaderView : null;
    }

    /**
     * Returns true if the item at the specified position is not a separator
     * (A separator is a non-selectable, non-clickable item).
     *
     * @param position Index of the item
     * @return True if the item is not a separator
     */
    public boolean isEnabled(int position) {
        return false;
    }
}

