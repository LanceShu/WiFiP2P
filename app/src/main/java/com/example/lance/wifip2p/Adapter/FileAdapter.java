package com.example.lance.wifip2p.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lance.wifip2p.Content;
import com.example.lance.wifip2p.DataBean.FileBean;
import com.example.lance.wifip2p.DataBean.SendFileBean;
import com.example.lance.wifip2p.R;

import java.util.List;

/**
 * Created by Lance
 * on 2017/11/29.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private Context context;
    private List<FileBean> fileBeans;
    private int mipmapID;

    public FileAdapter(Context context, List<FileBean> fileBeans , int mipmapID){
        this.context = context;
        this.fileBeans = fileBeans;
        this.mipmapID = mipmapID;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int filePosition = fileBeans.size() - position - 1;
        final FileBean fileBean = fileBeans.get(filePosition);
        if(fileBean.isFileSelected()){
            holder.fileSelected.setImageResource(R.mipmap.select2);
        }else{
            holder.fileSelected.setImageResource(R.mipmap.select1);
        }
        Glide.with(context)
                .load(mipmapID)
                .centerCrop()
                .error(R.mipmap.ic_launcher)
                .into(holder.fileImage);

        holder.fileName.setText(fileBean.getFileName());
        if (fileBean.getFileSize().equals("unknown")) {
            holder.fileSize.setText(fileBean.getFileSize());
        } else {
            long fileSize = Long.parseLong(fileBean.getFileSize());
            double size = (double) (fileSize) / 1024 / 1024;
            holder.fileSize.setText((size + "").substring(0, 4) + " M");
        }
        holder.fileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendFileBean sendFileBean = new SendFileBean();
                if(fileBean.isFileSelected()){
                    holder.fileSelected.setImageResource(R.mipmap.select1);
                    fileBeans.get(filePosition).setFileSelected(false);
                    if (Content.selectedFileList != null && Content.selectedFileList.size() != 0) {
                        for (int i = 0; i < Content.selectedFileList.size(); i++) {
                            if (Content.selectedFileList.get(i).getSendPath().equals(fileBeans.get(filePosition).getFilePath())) {
                                Content.selectedFileList.remove(i);
                                break;
                            }
                        }
                    }
                }else{
                    holder.fileSelected.setImageResource(R.mipmap.select2);
                    fileBeans.get(filePosition).setFileSelected(true);
                    sendFileBean.setSendName(fileBean.getFileName());
                    sendFileBean.setSendPath(fileBean.getFilePath());
                    sendFileBean.setSendSize(fileBean.getFileSize());
                    sendFileBean.setSendIcon(mipmapID + "");
                    sendFileBean.setSendType("file");
                    Content.selectedFileList.add(sendFileBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileBeans.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout fileLayout;
        private ImageView fileSelected;
        private ImageView fileImage;
        private TextView fileName;
        private TextView fileSize;

        ViewHolder(View view) {
            super(view);
            fileLayout = view.findViewById(R.id.media_layout);
            fileSelected = view.findViewById(R.id.media_isSeleted);
            fileImage = view.findViewById(R.id.media_image);
            fileName = view.findViewById(R.id.media_name);
            fileSize = view.findViewById(R.id.media_size);
        }
    }
}
