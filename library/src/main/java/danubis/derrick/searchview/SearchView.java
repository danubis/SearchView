package danubis.derrick.searchview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;


public class SearchView extends LinearLayout implements View.OnClickListener {

    public static final int ANIMATION_DURATION = 300;

    private static int mIconColor = Color.BLACK;
    private static int mTextColor = Color.BLACK;
    private static int mTextHighlightColor = Color.BLACK;
    private static int mTextStyle = Typeface.NORMAL;
    private static Typeface mTextFont = Typeface.DEFAULT;

    private final Context mContext;

    private RecyclerView.Adapter mAdapter = null;
    private OnQueryTextListener mOnQueryChangeListener = null;
    private OnOpenCloseListener mOnOpenCloseListener = null;
    private RecyclerView mRecyclerView;
    private View mDividerView;
    private SearchEditText mSearchEditText;
    private ImageView mSearchImageView;
    private ImageView mEmptyImageView;
    private LinearLayout mContainer;
    private LinearLayout mLinearLayout;
    private CharSequence mOldQueryText;
    private CharSequence mUserQuery = "";
    private int mAnimationDuration = ANIMATION_DURATION;
    private float moveDistance = 150f;
    private boolean mIsSearchOpen = false;
    private boolean mShouldClearOnOpen = false;
    private boolean mShouldClearOnClose = false;
    private boolean mShouldHideOnKeyboardClose = true;

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
        initStyle(attrs, defStyleAttr);
    }

    public static int getIconColor() {
        return mIconColor;
    }

    public void setIconColor(@ColorInt int color) {
        mIconColor = color;
        ColorFilter colorFilter = new PorterDuffColorFilter(mIconColor, PorterDuff.Mode.SRC_IN);
        mEmptyImageView.setColorFilter(colorFilter);
    }

    public static int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(@ColorInt int color) {
        mTextColor = color;
    }

    public static int getTextHighlightColor() {
        return mTextHighlightColor;
    }

    public void setTextHighlightColor(@ColorInt int color) {
        mTextHighlightColor = color;
    }

    public static Typeface getTextFont() {
        return mTextFont;
    }

    public static int getTextStyle() {
        return mTextStyle;
    }

    public void setTextStyle(int style) {
        mTextStyle = style;
        mSearchEditText.setTypeface((Typeface.create(mTextFont, mTextStyle)));
    }

    private void initView() {
        inflate(mContext, R.layout.search_view, this);

        mContainer = (LinearLayout) findViewById(R.id.container);
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_result);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setVisibility(View.GONE);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressWarnings("StatementWithEmptyBody")
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    hideKeyboard();
                } else {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    }
                }
            }
        });

        mDividerView = findViewById(R.id.view_divider);
        mDividerView.setVisibility(View.GONE);

        mSearchImageView = (ImageView) findViewById(R.id.imageView_search);
        mSearchImageView.setImageResource(R.drawable.ic_search_black_24dp);
        mSearchImageView.setOnClickListener(this);
        mSearchImageView.setVisibility(View.VISIBLE);

        mEmptyImageView = (ImageView) findViewById(R.id.imageView_clear);
        mEmptyImageView.setImageResource(R.drawable.ic_clear_black_24dp);
        mEmptyImageView.setOnClickListener(this);
        mEmptyImageView.setVisibility(View.GONE);

        mSearchEditText = (SearchEditText) findViewById(R.id.searchEditText_input);
        mSearchEditText.setSearchView(this);
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SearchView.this.onTextChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                onSubmitQuery();
                return true;
            }
        });
        mSearchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    addFocus();
                } else {
                    removeFocus();
                }
            }
        });
    }

    private void initStyle(AttributeSet attrs, int defStyleAttr) {
        final TypedArray attr = mContext.obtainStyledAttributes(attrs, R.styleable.SearchView, defStyleAttr, 0);
        if (attr != null) {
            if (attr.hasValue(R.styleable.SearchView_search_height)) {
                setHeight(attr.getDimension(R.styleable.SearchView_search_height, 0));
            }
            if (attr.hasValue(R.styleable.SearchView_search_icon_color)) {
                setIconColor(attr.getColor(R.styleable.SearchView_search_icon_color, 0));
            }
            if (attr.hasValue(R.styleable.SearchView_search_text_color)) {
                setTextColor(attr.getColor(R.styleable.SearchView_search_text_color, 0));
            }
            if (attr.hasValue(R.styleable.SearchView_search_text_highlight_color)) {
                setTextHighlightColor(attr.getColor(R.styleable.SearchView_search_text_highlight_color, 0));
            }
            if (attr.hasValue(R.styleable.SearchView_search_text_size)) {
                setTextSize(attr.getDimension(R.styleable.SearchView_search_text_size, 0));
            }
            if (attr.hasValue(R.styleable.SearchView_search_text_style)) {
                setTextStyle(attr.getInt(R.styleable.SearchView_search_text_style, 0));
            }
            if (attr.hasValue(R.styleable.SearchView_search_hint)) {
                setHint(attr.getString(R.styleable.SearchView_search_hint));
            }
            if (attr.hasValue(R.styleable.SearchView_search_hint_color)) {
                setHintColor(attr.getColor(R.styleable.SearchView_search_hint_color, 0));
            }
            if (attr.hasValue(R.styleable.SearchView_search_divider)) {
                setDivider(attr.getBoolean(R.styleable.SearchView_search_divider, false));
            }
            if (attr.hasValue(R.styleable.SearchView_search_animation_duration)) {
                setAnimationDuration(attr.getInteger(R.styleable.SearchView_search_animation_duration, mAnimationDuration));
            }
            if (attr.hasValue(R.styleable.SearchView_search_clear_on_open)) {
                setShouldClearOnOpen(attr.getBoolean(R.styleable.SearchView_search_clear_on_open, false));
            }
            if (attr.hasValue(R.styleable.SearchView_search_clear_on_close)) {
                setShouldClearOnClose(attr.getBoolean(R.styleable.SearchView_search_clear_on_close, true));
            }
            if (attr.hasValue(R.styleable.SearchView_search_hide_on_keyboard_close)) {
                setShouldHideOnKeyboardClose(attr.getBoolean(R.styleable.SearchView_search_hide_on_keyboard_close, true));
            }
            if (attr.hasValue(R.styleable.SearchView_search_cursor_drawable)) {
                setCursorDrawable(attr.getResourceId(R.styleable.SearchView_search_cursor_drawable, 0));
            }
            attr.recycle();
        }
    }

    public void setMoveDistance(float distance) {
        moveDistance = distance;
    }

    public void setTextOnly(CharSequence text) {
        mSearchEditText.setText(text);
    }

    public CharSequence getTextOnly() {
        return mSearchEditText.getText();
    }

    public void setTextOnly(@StringRes int text) {
        mSearchEditText.setText(text);
    }

    public void setQuery(CharSequence query, boolean submit) {
        setQueryWithoutSubmitting(query);

        if (!TextUtils.isEmpty(mUserQuery)) {
            mSearchImageView.setVisibility(VISIBLE);
            mEmptyImageView.setVisibility(View.GONE);
        }

        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery();
        }
    }

    public void setQuery(@StringRes int query, boolean submit) {
        setQuery(String.valueOf(query), submit);
    }

    public CharSequence getQuery() {
        return mSearchEditText.getText();
    }

    public void setHint(@Nullable CharSequence hint) {
        mSearchEditText.setHint(hint);
    }

    @Nullable
    public CharSequence getHint() {
        return mSearchEditText.getHint();
    }

    public void setHint(@StringRes int hint) {
        mSearchEditText.setHint(hint);
    }

    public int getImeOptions() {
        return mSearchEditText.getImeOptions();
    }

    public void setImeOptions(int imeOptions) {
        mSearchEditText.setImeOptions(imeOptions);
    }

    public int getInputType() {
        return mSearchEditText.getInputType();
    }

    public void setInputType(int inputType) {
        mSearchEditText.setInputType(inputType);
    }

    public RecyclerView.Adapter getAdapter() {
        return mRecyclerView.getAdapter();
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
        mRecyclerView.setAdapter(mAdapter);
    }

    public boolean getShouldClearOnClose() {
        return mShouldClearOnClose;
    }

    public void setShouldClearOnClose(boolean shouldClearOnClose) {
        mShouldClearOnClose = shouldClearOnClose;
    }

    public boolean getShouldClearOnOpen() {
        return mShouldClearOnOpen;
    }

    public void setShouldClearOnOpen(boolean shouldClearOnOpen) {
        mShouldClearOnOpen = shouldClearOnOpen;
    }

    public boolean getShouldHideOnKeyboardClose() {
        return mShouldHideOnKeyboardClose;
    }

    public void setShouldHideOnKeyboardClose(boolean shouldHideOnKeyboardClose) {
        mShouldHideOnKeyboardClose = shouldHideOnKeyboardClose;
    }

    public void setHeight(float dp) {
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        ViewGroup.LayoutParams params = mLinearLayout.getLayoutParams();
        params.height = height;
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        mLinearLayout.setLayoutParams(params);
    }

    public void setTextSize(float size) {
        mSearchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setHintColor(@ColorInt int color) {
        mSearchEditText.setHintTextColor(color);
    }

    public void setDivider(boolean divider) {
        if (divider) {
            mRecyclerView.addItemDecoration(new SearchDivider(mContext));
        } else {
            mRecyclerView.removeItemDecoration(new SearchDivider(mContext));
        }
    }

    public void setAnimationDuration(int animationDuration) {
        mAnimationDuration = animationDuration;
    }

    public void setCursorDrawable(@DrawableRes int drawable) {
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            try {
                f.set(mSearchEditText, drawable);
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void open() {
        if (mShouldClearOnOpen && mSearchEditText.length() > 0) {
            mSearchEditText.getText().clear();
        }
        mSearchEditText.requestFocus();
    }

    public void close() {
        if (mShouldClearOnClose && mSearchEditText.length() > 0) {
            mSearchEditText.getText().clear();
        }
        mSearchEditText.clearFocus();
    }

    public void addFocus() {
        mIsSearchOpen = true;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mOnOpenCloseListener != null) {
                    mOnOpenCloseListener.onOpen();
                }
            }
        }, mAnimationDuration);
        SearchAnimator.rise(this, moveDistance, mAnimationDuration);
        showKeyboard();

        if (!TextUtils.isEmpty(mUserQuery)) {
            mSearchImageView.setVisibility(GONE);
            mEmptyImageView.setVisibility(View.VISIBLE);
        }
        showSuggestions();
        mContainer.setBackground(mContext.getDrawable(R.drawable.search_view_opened_bg));
    }

    public void removeFocus() {
        mIsSearchOpen = false;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mOnOpenCloseListener != null) {
                    mOnOpenCloseListener.onClose();
                }
            }
        }, mAnimationDuration);
        SearchAnimator.fall(this, moveDistance, mAnimationDuration);
        hideKeyboard();

        if (!TextUtils.isEmpty(mUserQuery)) {
            mSearchImageView.setVisibility(VISIBLE);
            mEmptyImageView.setVisibility(View.GONE);
        }
        hideSuggestions();
        mContainer.setBackground(mContext.getDrawable(R.drawable.search_view_bg));
    }

    public void showSuggestions() {
        if (mAdapter != null && mAdapter.getItemCount() > 0) {
            mDividerView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            SearchAnimator.fadeIn(mRecyclerView, mAnimationDuration);
        }
    }

    public void hideSuggestions() {
        if (mAdapter != null) {
            mDividerView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            SearchAnimator.fadeOut(mRecyclerView, mAnimationDuration);
        }
    }

    public boolean isSearchOpen() {
        return mIsSearchOpen;
    }

    public void showKeyboard() {
        if (!isInEditMode()) {
            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(mSearchEditText, 0);
            inputManager.showSoftInput(this, 0);
        }
    }

    public void hideKeyboard() {
        if (!isInEditMode()) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    private void onTextChanged(CharSequence newText) {
        if (newText.equals(mOldQueryText)) {
            return;
        }
        CharSequence text = mSearchEditText.getText();
        mUserQuery = text;

        if (mAdapter != null && mAdapter instanceof Filterable) {
            ((Filterable) mAdapter).getFilter().filter(text);
        }

        if (!TextUtils.isEmpty(mUserQuery)) {
            showSuggestions();
            mSearchImageView.setVisibility(GONE);
            mEmptyImageView.setVisibility(View.VISIBLE);
        } else {
            hideSuggestions();
            mSearchImageView.setVisibility(VISIBLE);
            mEmptyImageView.setVisibility(View.GONE);
        }

        if (mOnQueryChangeListener != null && !TextUtils.equals(newText, mOldQueryText)) {
            mOnQueryChangeListener.onQueryTextChange(newText.toString());
        }
        mOldQueryText = newText.toString();
    }

    private void setQueryWithoutSubmitting(CharSequence query) {
        mSearchEditText.setText(query);
        if (query != null) {
            mSearchEditText.setSelection(mSearchEditText.length());
            mUserQuery = query;
        } else {
            mSearchEditText.getText().clear(); // mSearchEditText.setText("");
        }
    }

    private void onSubmitQuery() {
        CharSequence query = mSearchEditText.getText();
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            if (mOnQueryChangeListener == null || !mOnQueryChangeListener.onQueryTextSubmit(query.toString())) {
                mSearchEditText.setText(query);
            }
        }
    }

    private int getCenterX(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location[0] + view.getWidth() / 2;
    }

    @Override
    public void onClick(View v) {
        if (v == mEmptyImageView) {
            if (mSearchEditText.length() > 0) {
                mSearchEditText.getText().clear();
                showSuggestions();
            }
        } else if (v == mSearchImageView) {
            mSearchEditText.requestFocus();
        }
    }

    public void setOnQueryTextListener(OnQueryTextListener listener) {
        mOnQueryChangeListener = listener;
    }

    @SuppressWarnings({"UnusedReturnValue", "SameReturnValue", "UnusedParameters"})
    public interface OnQueryTextListener {
        boolean onQueryTextSubmit(String query);

        boolean onQueryTextChange(String newText);
    }

    public void setOnOpenCloseListener(OnOpenCloseListener listener) {
        mOnOpenCloseListener = listener;
    }

    @SuppressWarnings({"UnusedReturnValue", "SameReturnValue"})
    public interface OnOpenCloseListener {
        boolean onClose();

        boolean onOpen();
    }
}

