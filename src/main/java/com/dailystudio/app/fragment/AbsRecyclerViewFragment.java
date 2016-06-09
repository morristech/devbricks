package com.dailystudio.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import com.dailystudio.app.widget.DeferredHandler;
import com.dailystudio.development.Logger;

public abstract class AbsRecyclerViewFragment<Item, ItemSet, ItemHolder extends RecyclerView.ViewHolder>
		extends AbsLoaderFragment<ItemSet>
	implements OnItemClickListener {
	
	public interface OnListItemSelectedListener {
		
        public void onListItemSelected(Object itemData);
        
    }

	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter<ItemHolder> mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	
    private OnListItemSelectedListener mOnListItemSelectedListener;
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		bindAdapterView();
	}

	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		if (mOnListItemSelectedListener != null) {
			final RecyclerView.Adapter adapter = getAdapter();
			if (adapter == null) {
				return;
			}
			
//			Object data = adapter.getItem(position);
//
//			mOnListItemSelectedListener.onListItemSelected(data);
		}
	}

	protected int getAdapterViewId() {
		return android.R.id.list;
	}
	
	@SuppressWarnings("unchecked")
    protected void bindAdapterView() {
		final View fragmentView = getView();
		if (fragmentView == null) {
			return;
		}

		RecyclerView oldRecyclerView = mRecyclerView;

		if (oldRecyclerView != null) {
			oldRecyclerView.clearDisappearingChildren();
			oldRecyclerView.clearAnimation();
			oldRecyclerView.setAdapter(null);
//			oldRecyclerView.setOnItemClickListener(null);
			oldRecyclerView.setVisibility(View.GONE);
//			oldRecyclerView.setEmptyView(null);
			oldRecyclerView.setLayoutManager(null);
		}
		
		mAdapter = onCreateAdapter();
		mLayoutManager = onCreateLayoutManager();
		
		mRecyclerView = (RecyclerView) fragmentView.findViewById(
		        getAdapterViewId());
		if (mRecyclerView != null) {
			mRecyclerView.setAdapter(mAdapter);
			mRecyclerView.setLayoutManager(mLayoutManager);
//			mRecyclerView.setOnItemClickListener(this);
			mRecyclerView.setVisibility(View.VISIBLE);
			mRecyclerView.scheduleLayoutAnimation();
			
//			final View emptyView = fragmentView.findViewById(getEmptyViewId());
//			if (emptyView != null) {
//				mRecyclerView.setEmptyView(emptyView);
//			}
		}
	}
	
	public RecyclerView.Adapter<ItemHolder> getAdapter() {
		return mAdapter;
	}
	
	public RecyclerView getRecyclerView() {
		return mRecyclerView;
	}
	
 	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        if (activity instanceof OnListItemSelectedListener) {
        	mOnListItemSelectedListener = (OnListItemSelectedListener) activity;
        } else {
        	Logger.warnning("host activity does not implements: %s", 
        			OnListItemSelectedListener.class.getSimpleName());
        }
    }

 	@Override
 	public void onLoadFinished(Loader<ItemSet> loader, ItemSet data) {
 		bindData(mAdapter, data);
 	};
 	
 	@Override
 	public void onLoaderReset(Loader<ItemSet> loader) {
 		bindData(mAdapter, null);
 	};
 	
 	protected void removeCallbacks(Runnable r) {
 		mHandler.removeCallbacks(r);
 	}
 	
 	protected void post(Runnable r) {
 		mHandler.post(r);
 	}
 	
 	protected void postDelayed(Runnable r, long delayMillis) {
 		mHandler.postDelayed(r, delayMillis);
 	}
 	
	protected void notifyAdapterChanged() {
		post(notifyAdapterChangedRunnable);
	}
 	
	protected void notifyAdapterChangedOnIdle() {
		mDeferredHandler.postIdle(notifyAdapterChangedRunnable);
	}
 	
	abstract protected void bindData(RecyclerView.Adapter adapter, ItemSet data);

	abstract protected RecyclerView.Adapter onCreateAdapter();
	abstract protected RecyclerView.LayoutManager onCreateLayoutManager();

	private Handler mHandler = new Handler();
	private DeferredHandler mDeferredHandler = new DeferredHandler();
	
	private Runnable notifyAdapterChangedRunnable = new Runnable() {
		
		@Override
		public void run() {
			if (mAdapter == null) {
				return;
			}
			
			mAdapter.notifyDataSetChanged();
		}
		
	};

}
