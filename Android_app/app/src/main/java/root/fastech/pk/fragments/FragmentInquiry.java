package root.fastech.pk.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.material.components.R;
import com.material.components.activity.profile.ProfileFabMenu;
import com.material.components.activity.profile.ProfileImageAppbar;
import com.material.components.adapter.AdapterListInbox;
import com.material.components.data.DataGenerator;
import com.material.components.model.Inbox;
import com.material.components.model.People;
import com.material.components.utils.ItemAnimation;
import com.material.components.utils.Tools;
import com.material.components.utils.ViewAnimation;
import com.material.components.widget.LineItemDecoration;

import org.w3c.dom.Text;

import java.util.List;

import root.fastech.pk.adapters.AdapterInquiry;
import root.fastech.pk.home.StudentsMain;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class FragmentInquiry extends Fragment {


    public FragmentInquiry() {

    }

    public static FragmentInquiry newInstance() {
        FragmentInquiry fragment = new FragmentInquiry();
        return fragment;
    }


    //Action bar delete menu
    private ActionModeCallback_Inquiry actionModeCallback;
    private ActionMode actionMode;

    //Data
    public RecyclerView recyclerView;
    public Toolbar toolbar;
    public AdapterInquiry mAdapter;

    //Bottom Sheet
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;

    //List Animation
    private int animation_type = ItemAnimation.FADE_IN;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_student_inquiry, container, false);

        loadingAndDisplayContent(1500,root);

        return root;
    }


    private void loadingAndDisplayContent(long LOADING_DURATION, final View root) {
        final LinearLayout lyt_progress = (LinearLayout) root.findViewById(R.id.lyt_progress);
        lyt_progress.setVisibility(View.VISIBLE);
        lyt_progress.setAlpha(1.0f);

        RecyclerView rv = (RecyclerView) root.findViewById(R.id.recyclerView);
        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab_add);
        rv.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewAnimation.fadeOut(lyt_progress);
            }
        }, LOADING_DURATION);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initComponent(root);
            }
        }, LOADING_DURATION + 400);
    }

    private void initComponent(View root) {

        RecyclerView rv = (RecyclerView) root.findViewById(R.id.recyclerView);
        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab_add);
        rv.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);



        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Inquiry Add clicked", Toast.LENGTH_SHORT).show(); //Search
            }
        });

        //Bottom Sheet
        bottom_sheet = root.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

        initData();

    }

    private void initData() {


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new LineItemDecoration(getContext(), LinearLayout.VERTICAL));
        recyclerView.setHasFixedSize(true);

        List<Inbox> items = DataGenerator.getInboxData(getContext());

        //set data and list adapter
        //mAdapter = new AdapterInquiry(getContext(), items, animation_type);
        recyclerView.setAdapter(mAdapter);

        //Multi Select
//        mAdapter.setOnClickListener(new AdapterInquiry.OnClickListener() {
//
//            @Override
//            public void onItemClick(View view, Inbox obj, int pos) {
//                if (mAdapter.getSelectedItemCount() > 0) {
//                    enableActionMode(pos);
//                } else {
//                    // read the inbox which removes bold from the row
//                    //Inbox inbox = mAdapter.getItem(pos);
//                    //Toast.makeText(getContext(), "Read: " + inbox.from, Toast.LENGTH_SHORT).show();
//                    showBottomSheetDialog(obj);
//                }
//            }
//
//            @Override
//            public void onItemLongClick(View view, Inbox obj, int pos) {
//                enableActionMode(pos);
//            }
//        });

        actionModeCallback = new ActionModeCallback_Inquiry();

    }

    private void showBottomSheetDialog(final Inbox people) {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.sheet_student_menu, null);

        ((View) view.findViewById(R.id.lyt_preview)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Preview '" + people.from + "' clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ProfileImageAppbar.class);
                String clicked_id = people.id + "";
                intent.putExtra(EXTRA_MESSAGE, clicked_id);
                startActivity(intent);
                mBottomSheetDialog.dismiss();

            }
        });

        ((View) view.findViewById(R.id.lyt_share)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Edit '" + people.from + "' clicked", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });

        ((View) view.findViewById(R.id.lyt_get_link)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveDialog();
                mBottomSheetDialog.dismiss();
            }
        });


        mBottomSheetDialog = new BottomSheetDialog(getContext());
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }


    //For Multi Selection
    private void enableActionMode(int position) {
        if (actionMode == null) {
            StudentsMain s = new StudentsMain();
            actionMode =  s.startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback_Inquiry implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Tools.setSystemBarColor(getActivity(), R.color.red_600); //Color on delete bar
            mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_delete) {
                deleteInboxes();
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections();
            actionMode = null;
            Tools.setSystemBarColor(getActivity(), R.color.blue_600);
        }
    }

    private void deleteInboxes() {
        List<Integer> selectedItemPositions = mAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mAdapter.removeData(selectedItemPositions.get(i));
        }
        mAdapter.notifyDataSetChanged();
    }

    private void showRemoveDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_remove);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_remove)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Removed...", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }





}