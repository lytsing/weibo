/*
 * Copyright (C) 2013 http://lytsing.org
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

package org.lytsing.android.weibo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lytsing.android.weibo.R;

import androidx.fragment.app.ListFragment;

/**
 * Menu Fragment.
 * @author Liqing Huang
 */
public class MenuFragment extends ListFragment {

    private String[] mListItems = new String[] {"Home", "Settings", "Search", "Help"};

    private int[] mIcons = new int[]{
            R.drawable.ic_menu_home,
            android.R.drawable.ic_menu_preferences,
            android.R.drawable.ic_menu_search,
            android.R.drawable.ic_menu_help
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < mListItems.length; i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("values", mListItems[i]);
            listItem.put("images", mIcons[i]);
            listItems.add(listItem);
        }
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), listItems,
                R.layout.slide, new String[] { "values", "images" },
                new int[] { R.id.slide_list_title, R.id.slide_list_icon });
        setListAdapter(adapter);
    }
}

