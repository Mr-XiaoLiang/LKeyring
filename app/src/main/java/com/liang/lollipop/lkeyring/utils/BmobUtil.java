package com.liang.lollipop.lkeyring.utils;

import com.liang.lollipop.lkeyring.bean.KeyBean;
import com.liang.lollipop.lkeyring.bean.UserBean;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Lollipop on 2017/08/28.
 * Bmob的相关业务类
 */
public class BmobUtil {

    public static void findUserById(String id, SaveListener<UserBean> listener){
        UserBean userBean = new UserBean();
        userBean.setUsername(id);
        userBean.setPassword("LKeyring");
        userBean.login(listener);
    }

    public static void addUserById(String id, SaveListener<UserBean> listener){
        UserBean userBean = new UserBean();
        userBean.setDriveId(id);
        userBean.setUsername(id);
        userBean.setPassword("LKeyring");
        userBean.setSignature("他还什么都没说。");
        userBean.signUp(listener);
    }

    public static void tryGetUser(String id, SaveListener<UserBean> listener){
        findUserById(id, new FindUserCallBack(listener,id));
    }

    private static class FindUserCallBack extends SaveListener<UserBean>{

        final SaveListener<UserBean> listener;
        final String id;

        public FindUserCallBack(SaveListener<UserBean> listener, String id) {
            this.listener = listener;
            this.id = id;
        }

        @Override
        public void done(UserBean bean, BmobException e) {
            if(e==null){//如果访问成功
                if(bean==null){//如果数据为空
                    addUserById(id, new AddUserCallBack(listener));
                }else{//如果数据不为空
                    listener.done(bean,e);//传递出去
                }
            }else{//如果访问失败
                if(e.getErrorCode()==101){
                    addUserById(id, new AddUserCallBack(listener));
                }else{
                    listener.done(bean,e);//传递出去
                }
            }
        }
    }

    private static class AddUserCallBack extends SaveListener<UserBean>{

        final SaveListener<UserBean> listener;

        public AddUserCallBack(SaveListener<UserBean> listener) {
            this.listener = listener;
        }

        @Override
        public void done(UserBean userBean, BmobException e) {
            if(e==null){//如果访问正常
                //创建数据，回传回去
                listener.done(userBean,e);
            }else{
                //出现异常，返回空
                listener.done(null,e);
            }
        }
    }

    public static void findKey(String id, FindListener<KeyBean> listener){
        String key = FetchKeyUtil.fetchKey(id);
        BmobQuery<KeyBean> query = new BmobQuery<>();
        query.setLimit(1);
        query.addWhereEqualTo("key_name",key);
        query.findObjects(listener);
    }

    public static void saveKey(String id,String value, SaveListener<String> listener){
        String key = FetchKeyUtil.fetchKey(id);
        String userId = UserBean.getCurrentUser(UserBean.class).getObjectId();
        KeyBean bean = new KeyBean();
        bean.setCreate_id(userId);
        bean.setKey_value(value);
        bean.setKey_name(key);
        bean.save(listener);
    }

}

