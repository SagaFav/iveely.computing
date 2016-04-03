package com.iveely.computing.node;

import com.iveely.computing.api.TopologySubmitter;
import com.iveely.computing.common.Message;
import com.iveely.computing.status.SystemConfig;
import com.iveely.framework.compile.JarExecutor;
import com.iveely.framework.net.Packet;
import com.iveely.framework.net.SyncServer;
import com.iveely.framework.text.Convertor;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.apache.log4j.Logger;

/**
 * Event process on slave.
 *
 * @author sea11510@mail.ustc.edu.cn
 */
public class SlaveProcessor implements SyncServer.ICallback {

	// /**
    // * Distribute memory cache.
    // */
    // private final CyclingBuffer cache;
    /**
     * The observer of distribute cache.
     */
    private final TreeMap<String, List<String>> observers;

    /**
     * Logger.
     */
    private final Logger logger = Logger.getLogger(SlaveProcessor.class.getName());

    public SlaveProcessor() {
		// cache = new CyclingBuffer(10000 * 1000);
        // TODO:If the cache was covered by other key. please change the
        // observer.
        observers = new TreeMap<>();
    }

    @Override
    public Packet invoke(Packet packet) {
        Message.ExecuteType executeType = Message.getExecuteType(packet.getExecutType());
        logger.info(executeType.toString());

        // 1. Process upload.
        if (executeType == Message.ExecuteType.UPLOAD) {
            return processUploadApp(packet);
        }

		// // 3. Process add new key-val.
        // if (executType == Message.ExecuteType.SETCACHE) {
        // String[] infor = Message.getString(packet.getData()).split("#k-v#");
        // String key = infor[0];
        // String val = infor[1];
        // cache.add(key, val);
        // if (observers.containsKey(key)) {
        // //TODO:Send to observers, if observer is not exist,please remove it.
        // List<String> observerAddress = observers.get(key);
        // observerAddress.stream().forEach((String observer) -> {
        // try {
        // String[] obsIpAndPort = observer.split(":");
        // Client client = new Client(obsIpAndPort[0],
        // Integer.parseInt(obsIpAndPort[1]));
        // InternetPacket callbackPacket = new InternetPacket();
        // callbackPacket.setExecutType(Message.ExecuteType.CALLBACKOFKEYEVENT.ordinal());
        // callbackPacket.setMimeType(Message.MIMEType.MESSAGE.ordinal());
        // callbackPacket.setData(Message.getBytes(key + "#k-v#" + val));
        // client.send(packet);
        // } catch (NumberFormatException e) {
        // observerAddress.remove(observer);
        // }
        // });
        // }
        // return buildResponse("key is set success.",
        // Message.ExecuteType.RESPSETCACHE);
        // }
        //
        // // 4. Process get the value.
        // if (executType == Message.ExecuteType.GETCACHE) {
        // String key = Message.getString(packet.getData());
        // Object val = cache.read(key);
        // String resp = "";
        // if (val != null) {
        // resp = val.toString();
        // }
        // return buildResponse(resp, Message.ExecuteType.RESPGETCACHE);
        // }
        //
        // // 5. Prcess append value to specify key.
        // if (executType == Message.ExecuteType.APPENDCACHE) {
        // String[] infor = Message.getString(packet.getData()).split("#k-v#");
        // String key = infor[0];
        // String val = infor[1];
        // Object objVal = cache.read(key);
        // if (objVal != null) {
        // val = objVal + "#RECORD#" + val;
        // }
        // cache.add(key, val);
        // if (observers.containsKey(key)) {
        // //TODO:Send to observers, if observer is not exist,please remove it.
        // List<String> observerAddress = observers.get(key);
        // for (String observer : observerAddress) {
        // try {
        // String[] obsIpAndPort = observer.split(":");
        // Client client = new Client(obsIpAndPort[0],
        // Integer.parseInt(obsIpAndPort[1]));
        // InternetPacket callbackPacket = new InternetPacket();
        // callbackPacket.setExecutType(Message.ExecuteType.CALLBACKOFKEYEVENT.ordinal());
        // callbackPacket.setMimeType(Message.MIMEType.MESSAGE.ordinal());
        // callbackPacket.setData(Message.getBytes(key + "#k-v#" + val));
        // client.send(packet);
        // } catch (NumberFormatException e) {
        // observerAddress.remove(observer);
        // }
        //
        // }
        // }
        // return buildResponse("key is append success.",
        // Message.ExecuteType.RESPSETCACHE);
        // }
        // 2. Process register memory service.
        if (executeType == Message.ExecuteType.REGISTE) {
            String[] infor = Message.getString(packet.getData()).split("_");
            String key = infor[0];
            String observerAddress = infor[1];
            if (observers.containsKey(key)) {
                List<String> list = observers.get(key);
                list.add(observerAddress);
            } else {
                List<String> list = new ArrayList<>();
                list.add(observerAddress);
                observers.put(key, list);
            }
            return buildResponse("Regist success.", Message.ExecuteType.RESPREGIST);
        }

        // 8. Process kill task.
        if (executeType == Message.ExecuteType.KILLTASK) {
            String tpName = Message.getString(packet.getData());
            String respStr = TopologySubmitter.stop(tpName);
            return buildResponse(respStr, Message.ExecuteType.RESPKILLTASK);
        }

        // 9. Check is online.
        if (executeType == Message.ExecuteType.ISONLINE) {
            return buildResponse("On Line", Message.ExecuteType.RESPISONLINE);
        }

        // 10. Rebalance task.
        if (executeType == Message.ExecuteType.REBALANCE) {
            String tpName = Message.getString(packet.getData());
            String respStr = TopologySubmitter.stop(tpName);
            return buildResponse(respStr, Message.ExecuteType.RESPREBALANCE);
        }

        return Packet.getUnknowPacket();
    }

    /**
     * Process upload application.
     *
     * @return
     */
    private Packet processUploadApp(Packet packet) {

        // 1. Extract application's name.
        byte[] data = packet.getData();
        byte[] lengthOfName = new byte[4];
        System.arraycopy(data, 0, lengthOfName, 0, 4);
        int argSize = Convertor.bytesToInt(lengthOfName);
        byte[] argBytes = new byte[argSize];
        System.arraycopy(data, 4, argBytes, 0, argSize);
        String arguments = Message.getString(argBytes);
        String[] runArgs = arguments.split(":");
        String responseInfo = "";

        // 2. Extract data.
        byte[] content = new byte[data.length - 4 - argSize];
        System.arraycopy(data, argSize + 4, content, 0, content.length);
        String uploadPath = SystemConfig.appFoler + "/" + runArgs[0] + ".slave.jar";
        try (DataOutputStream dbAppender = new DataOutputStream(new FileOutputStream(uploadPath, false))) {
            dbAppender.write(content);
        } catch (Exception e) {
            responseInfo += e.toString();
            Packet respPacket = new Packet();
            respPacket.setData(Message.getBytes(responseInfo));
            respPacket.setExecutType(Message.ExecuteType.RESPUPLOADAPP.ordinal());
            respPacket.setMimeType(Message.MIMEType.TEXT.ordinal());
            return respPacket;
        }

        // 3. Throw to thread pool and run it.
        JarExecutor executor;
        try {
            executor = new JarExecutor();
            String paramValue[] = {runArgs[1]};
            Class paramClass[] = {String.class};
            executor.invokeJarMain(uploadPath, runArgs[0], paramValue);
        } catch (Exception e) {
            responseInfo += e.getMessage();
        }

        // 4. Response packet.
        Packet respPacket = new Packet();
        respPacket.setData(Message.getBytes(responseInfo));
        respPacket.setExecutType(Message.ExecuteType.RESPUPLOADAPP.ordinal());
        respPacket.setMimeType(Message.MIMEType.TEXT.ordinal());
        return respPacket;
    }

    /**
     * Build response packet.
     *
     * @param responseData
     * @param responseExecutType
     * @return
     */
    private Packet buildResponse(String responseData, Message.ExecuteType responseExecutType) {
        Packet respPacket = new Packet();
        respPacket.setData(Message.getBytes(responseData));
        respPacket.setExecutType(responseExecutType.ordinal());
        respPacket.setMimeType(Message.MIMEType.TEXT.ordinal());
        return respPacket;
    }
}
