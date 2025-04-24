package ui.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.naojianghh.bilibili3.R;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private Context context;
    private List<String> searchResults;
    private OnItemClickListener listener;
    private String keyword;

    public SearchResultAdapter(Context context, List<String> searchResults) {
        this.searchResults = searchResults;
        this.context = context;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull SearchResultAdapter.ViewHolder holder, int position) {
        String result = searchResults.get(position);

        if (keyword != null && !keyword.isEmpty()){
            SpannableString spannableString = new SpannableString(result);
            int startIndex = result.toLowerCase().indexOf(keyword.toLowerCase());
            while (startIndex != -1){
                int endIndex = startIndex + keyword.length();
                int color = ContextCompat.getColor(context,R.color.bilibili);
                spannableString.setSpan(new ForegroundColorSpan(color),startIndex,endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                startIndex = result.toLowerCase().indexOf(keyword.toLowerCase(),endIndex);
            }
            holder.textView.setText(spannableString);
        } else {
            holder.textView.setText(result);
        }
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.resultTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(searchResults.get(position));
                        }
                    }
                }
            });
        }
    }
    public interface OnItemClickListener {
        void onItemClick(String result);
    }
}
