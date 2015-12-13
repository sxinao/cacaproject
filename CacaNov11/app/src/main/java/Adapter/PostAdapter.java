package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import entities.Post;
import entities.User;

import com.caca.R;

import org.w3c.dom.Text;


public class PostAdapter extends ArrayAdapter<String> {

    private List<Post> posts;
    private Context c;
    private LayoutInflater inflater;

    public PostAdapter(Context c, ArrayList<Post> posts) {
        super(c, R.layout.custom_items, (List) posts);
        this.c = c;
        this.posts = posts;
    }

    class ViewHolder {
        private ImageView ivimages;
        private TextView tvposts;
        private TextView tvlocations;
        private TextView tvtimestamp;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_items, null);
        }
        final ViewHolder holder = new ViewHolder();
        // init views
        holder.ivimages = (ImageView) convertView.findViewById(R.id.ivhead);
        holder.tvposts = (TextView) convertView.findViewById(R.id.tvpost);
        holder.tvlocations = (TextView) convertView.findViewById(R.id.tvlocation);
        holder.tvtimestamp = (TextView) convertView.findViewById(R.id.tvtimestamp);
        //assign data
        if (position < posts.size()) {
            holder.ivimages.setImageResource(posts.get(position).getImage()); //position
            holder.tvposts.setText(posts.get(position).getPostText());
            holder.tvlocations.setText(posts.get(position).getLocation() + ""); // default
            holder.tvtimestamp.setText(posts.get(position).getTimestamp());
            return convertView;
        }
        return convertView;
    }


}
