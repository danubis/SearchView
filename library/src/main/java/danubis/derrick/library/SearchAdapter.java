package danubis.derrick.library;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@SuppressWarnings({"unused", "WeakerAccess"})
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ResultViewHolder> implements Filterable {

    private List<SearchItem> mSuggestionsList = new ArrayList<>();
    private String key = "";
    private List<SearchItem> mResultList = new ArrayList<>();
    private List<OnItemClickListener> mItemClickListeners;

    // ---------------------------------------------------------------------------------------------
    public SearchAdapter(Context context) {
        getFilter().filter("");
    }

    public SearchAdapter(Context context, List<SearchItem> suggestionsList) {
        mSuggestionsList = suggestionsList;
        mResultList = suggestionsList;
        getFilter().filter("");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                if (!TextUtils.isEmpty(constraint)) {
                    key = constraint.toString().toLowerCase(Locale.getDefault());

                    List<SearchItem> results = new ArrayList<>();
                    List<SearchItem> history = new ArrayList<>();

                    history.addAll(mSuggestionsList);

                    for (SearchItem item : history) {
                        String string = item.get_text().toString().toLowerCase(Locale.getDefault());
                        if (string.contains(key)) {
                            results.add(item);
                        }// TODO ADVANCED COMPARE METHOD
                    }

                    if (results.size() > 0) {
                        filterResults.values = results;
                        filterResults.count = results.size();
                    }
                } else {
                    key = "";
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<SearchItem> dataSet = new ArrayList<>();

                if (results.count > 0) {
                    List<?> result = (ArrayList<?>) results.values;
                    for (Object object : result) {
                        if (object instanceof SearchItem) {
                            dataSet.add((SearchItem) object);
                        }
                    }
                } else {
                    if (key.isEmpty()) {
                        if (!mSuggestionsList.isEmpty()) {
                            dataSet = mSuggestionsList;
                        }
                    }
                }
                setData(dataSet);
            }
        };
    }

    @Override
    public ResultViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.search_item, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultViewHolder viewHolder, int position) {
        SearchItem item = mResultList.get(position);

        viewHolder.text.setTypeface((Typeface.create(SearchView.getTextFont(), SearchView.getTextStyle())));
        viewHolder.text.setTextColor(SearchView.getTextColor());

        String itemText = item.get_text().toString();
        String itemTextLower = itemText.toLowerCase(Locale.getDefault());

        if (itemTextLower.contains(key) && !key.isEmpty()) {
            SpannableString s = new SpannableString(itemText);
            s.setSpan(new ForegroundColorSpan(SearchView.getTextHighlightColor()), itemTextLower.indexOf(key), itemTextLower.indexOf(key) + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.text.setText(s, TextView.BufferType.SPANNABLE);
        } else {
            viewHolder.text.setText(item.get_text());
        }
    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public List<SearchItem> getSuggestionsList() {
        return mSuggestionsList;
    }

    // ---------------------------------------------------------------------------------------------
    public void setSuggestionsList(List<SearchItem> suggestionsList) {
        mSuggestionsList = suggestionsList;
        mResultList = suggestionsList;
    }

    @Deprecated
    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        addOnItemClickListener(mItemClickListener);
    }

    public void addOnItemClickListener(OnItemClickListener listener) {
        addOnItemClickListener(listener, null);
    }

    public void addOnItemClickListener(OnItemClickListener listener, Integer position) {
        if (mItemClickListeners == null)
            mItemClickListeners = new ArrayList<>();
        if (position == null)
            mItemClickListeners.add(listener);
        else
            mItemClickListeners.add(position, listener);
    }

    public void setData(List<SearchItem> data) {
        if (mResultList.size() == 0) {
            mResultList = data;
            if (data.size() != 0) {
                notifyItemRangeInserted(0, data.size());
            }
        } else {
            int previousSize = mResultList.size();
            int nextSize = data.size();
            mResultList = data;
            if (previousSize == nextSize && nextSize != 0)
                notifyItemRangeChanged(0, previousSize);
            else if (previousSize > nextSize) {
                if (nextSize == 0)
                    notifyItemRangeRemoved(0, previousSize);
                else {
                    notifyItemRangeChanged(0, nextSize);
                    notifyItemRangeRemoved(nextSize - 1, previousSize);
                }
            } else {
                notifyItemRangeChanged(0, previousSize);
                notifyItemRangeInserted(previousSize, nextSize - previousSize);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView text;

        public ResultViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.textView_item_text);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListeners != null) {
                for (OnItemClickListener listener : mItemClickListeners)
                    listener.onItemClick(v, getLayoutPosition());
            }
        }
    }

}