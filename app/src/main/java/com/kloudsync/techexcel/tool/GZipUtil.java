package com.kloudsync.techexcel.tool;

import android.text.TextUtils;
import android.util.Log;

import com.ub.techexcel.bean.DocumentAction;
import com.ub.techexcel.bean.SoundtrackBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class GZipUtil {

    public static final String GZIP_ENCODE_UTF_8 = "UTF-8";

    public static final String GZIP_ENCODE_ISO_8859_1 = "ISO-8859-1";
    public static final int SUB_SIZE=10;  //每一片的大小
    /**
     * 字符串的压缩
     *
     * @param str
     *            待压缩的字符串
     * @return 返回压缩后的字符串
     * @throws IOException
     */
    public static String compress(String str) throws IOException {
        if (null == str || str.length() <= 0) {
            return str;
        }
        // 创建一个新的输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 使用默认缓冲区大小创建新的输出流
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        // 将字节写入此输出流
        gzip.write(str.getBytes(GZIP_ENCODE_ISO_8859_1)); // 因为后台默认字符集有可能是GBK字符集，所以此处需指定一个字符集
        gzip.close();
        // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
        return out.toString(GZIP_ENCODE_ISO_8859_1);
    }

    /**
     * 字符串的解压
     *
     * @param str
     *            对字符串解压
     * @return 返回解压缩后的字符串
     * @throws IOException
     */
    public static String unCompress(String str) throws IOException {
        if (null == str || str.length() <= 0) {
            return str;
        }
        // 创建一个新的输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 创建一个 ByteArrayInputStream，使用 buf 作为其缓冲区数组
        ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes(GZIP_ENCODE_ISO_8859_1));
        // 使用默认缓冲区大小创建新的输入流
        GZIPInputStream gzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n = 0;

        // 将未压缩数据读入字节数组
        while ((n = gzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
        return out.toString(GZIP_ENCODE_ISO_8859_1);
    }



    public static List<List<JSONObject>>  fetchList(List<JSONObject> documentActionList){
        int listSize=documentActionList.size();
        int totleburst = listSize % SUB_SIZE== 0 ? listSize / SUB_SIZE: listSize / SUB_SIZE + 1;
        List<List<JSONObject>> subAryList = new ArrayList<>();
        for (int i = 0; i < totleburst; i++) {
            int index = i * SUB_SIZE;
            List<JSONObject> list = new ArrayList<>();
            int j = 0;
            while (j < SUB_SIZE && index < listSize) {
                list.add(documentActionList.get(index++));
                j++;
            }
            subAryList.add(list);
        }
        return subAryList;
    }


    public static List<DocumentAction>  getDocumentactionList(List<List<JSONObject>> subAryList, SoundtrackBean soundtrackBean){
        List<DocumentAction> documentActionsList=new ArrayList<>();
        for (int i = 0; i < subAryList.size(); i++) {
            List<JSONObject> subdata=subAryList.get(i);
            final JSONArray jsonArray=new JSONArray();
            for (int j = 0; j < subdata.size(); j++) {
                jsonArray.put(subdata.get(j));
            }
            String documnraction=jsonArray.toString();
            DocumentAction documentAction=new DocumentAction();
            documentAction.setAttachmentId(soundtrackBean.getAttachmentId());
            documentAction.setIndex(i+1);
            documentAction.setSyncId(soundtrackBean.getSoundtrackID());
            documentAction.setZippedActionData(documnraction);
            documentAction.setTotal(subAryList.size());
            documentActionsList.add(documentAction);
        }
        return documentActionsList;
    }

}

