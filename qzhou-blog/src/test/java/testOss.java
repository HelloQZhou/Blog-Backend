import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qzhou.QzhouBlogApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.InputStream;

@SuppressWarnings("all")
@SpringBootTest(classes = QzhouBlogApplication.class)
//@ConfigurationProperties(prefix = "oss")
public class testOss {
    private String accessKey;
    private String secretKey;
    private String bucket;

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    @Test
    public void testOss() {
        //����һ����ָ�� Region �����������
        Configuration cfg = new Configuration(Region.autoRegion());
        //cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// ָ����Ƭ�ϴ��汾
//...���������ο���ע��

        UploadManager uploadManager = new UploadManager(cfg);
//...�����ϴ�ƾ֤��Ȼ��׼���ϴ�
//        String accessKey = "P2DJIMWyoHg5UzWrFjSZ4g_sP-9wQpgHQ6uZSihs";
//        String secretKey = "cuUBumZGBGbMXjLg39uK0fctUt9NGoSo8gIxEYEy";
//        String bucket = "qzhoublog";

//ָ���ļ�����·������Ĭ�ϲ�ָ��key������£����ļ����ݵ�hashֵ��Ϊ�ļ���
        String key = "Qzhou/qzFile";

        try {
//            byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
//            ByteArrayInputStream byteInputStream=new ByteArrayInputStream(uploadBytes);
            //��ʱ·������������
            InputStream byteInputStream =new FileInputStream("C:\\Users\\zqqq\\Desktop\\Miko fox.png");
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            try {
                Response response = uploadManager.put(byteInputStream,key,upToken,null, null);
                //�����ϴ��ɹ��Ľ��
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception ex) {
            //ignore
        }

    }


}