package com.grampus.hualauncherkai.UI;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.grampus.hualauncherkai.fragment.AppFragment;

import java.util.List;

/**
 * Created by Grampus on 2017/4/18.
 */

public class PageFragmentAdpter extends FragmentStatePagerAdapter
{

    List<AppFragment> fragmentList;

    FragmentManager fm;


    public PageFragmentAdpter(FragmentManager fm, List<AppFragment> fl)
    {

        super(fm);
        this.fm = fm;
        fragmentList = fl;
    }

    @Override
    public Fragment getItem(int position)
    {
        return fragmentList.get(position);
    }

    @Override
    public int getCount()
    {
        return fragmentList.size();
    }

/*
    @Override
    public int getItemPosition(@NonNull Object object) {
        Log.w("EMMPageFra","getItemPosition--------------");
        return POSITION_NONE;//super.getItemPosition(object);
    }
/*
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //拿到缓存的fragment，如果没有缓存的，就新建一个，新建发生在fragment的第一次初始化时
        Log.w("EMMPageFra","instantiateItem----"+position);
        Fragment f = (Fragment) super.instantiateItem(container, position);
        String fragmentTag = f.getTag();
        if (f != getItem(position)) {
            //如果是新建的fragment，f 就和getItem(position)是同一个fragment，否则进入下面
            Log.w("EMMPageFra","instantiateItem--------------");
            FragmentTransaction ft = fm.beginTransaction();
            //移除旧的fragment
            ft.remove(f);
            //换成新的fragment
            f = getItem(position);
            //添加新fragment时必须用前面获得的tag
            ft.add(container.getId(), f, fragmentTag);
            ft.attach(f);
            ft.commitAllowingStateLoss();
        }
        return f;
    }
*/
}