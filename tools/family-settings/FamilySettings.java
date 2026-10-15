package family.settings;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public final class FamilySettings implements PreLaunchEntrypoint {
    static final String HOST="knowlescraft.duckdns.org";
    static final String LAN="10.0.0.133";
    public void onPreLaunch(){
        Path game=FabricLoader.getInstance().getGameDir();
        try{applyVoice(FabricLoader.getInstance().getConfigDir().resolve("plasmovoice/client.toml"));}catch(Exception e){System.err.println("[KnowlesCraft] Voice preference could not be applied.");}
        try{migrate(game);}catch(Exception e){System.err.println("[KnowlesCraft] Saved server could not be updated: "+e.getClass().getSimpleName());}
    }
    static void applyVoice(Path path)throws Exception{
        Files.createDirectories(path.getParent());String before=Files.exists(path)?Files.readString(path):"";
        String after=java.util.regex.Pattern.compile("(?m)^\\h*check_for_updates\\h*=").matcher(before).find()?before.replaceAll("(?m)^(\\h*check_for_updates\\h*=\\h*)true(?=\\h*(?:#|$))","$1false"):"check_for_updates = false\n"+before;
        if(!after.equals(before))Files.writeString(path,after);
    }
    static void migrate(Path game)throws Exception{
        migrate(game,chooseAddress());
    }
    static String chooseAddress(){return isOurServer(LAN,25565)?LAN:HOST;}
    static boolean isOurServer(String host,int port){
        try(Socket socket=new Socket()){
            socket.connect(new InetSocketAddress(host,port),700);socket.setSoTimeout(900);
            ByteArrayOutputStream handshake=new ByteArrayOutputStream();DataOutputStream body=new DataOutputStream(handshake);
            varInt(body,0);varInt(body,763);byte[] name=HOST.getBytes(StandardCharsets.UTF_8);varInt(body,name.length);body.write(name);body.writeShort(port);varInt(body,1);
            DataOutputStream out=new DataOutputStream(socket.getOutputStream());varInt(out,handshake.size());out.write(handshake.toByteArray());out.write(new byte[]{1,0});out.flush();
            DataInputStream in=new DataInputStream(socket.getInputStream());int packet=varInt(in);if(packet<1||packet>1048576||varInt(in)!=0)return false;
            int size=varInt(in);if(size<1||size>1048576)return false;byte[] bytes=in.readNBytes(size);if(bytes.length!=size)return false;
            String status=new String(bytes,StandardCharsets.UTF_8);
            return status.contains("KnowlesCraft")&&status.matches("(?s).*\\\"protocol\\\"\\s*:\\s*763(?:[,}]).*");
        }catch(Exception e){return false;}
    }
    static void varInt(DataOutputStream out,int value)throws IOException{do{int b=value&127;value>>>=7;out.writeByte(value==0?b:b|128);}while(value!=0);}
    static int varInt(DataInputStream in)throws IOException{int value=0;for(int shift=0;shift<35;shift+=7){int b=in.readUnsignedByte();value|=(b&127)<<shift;if((b&128)==0)return value;}throw new IOException("Invalid packet length");}
    static void migrate(Path game,String destination)throws Exception{
        if(!Set.of(HOST,LAN).contains(destination))throw new IllegalArgumentException("Unknown connection route");
        Path path=game.resolve("servers.dat");Tag root;String rootName="";boolean compressed=false;byte[] original=null;
        if(Files.exists(path)){
            original=Files.readAllBytes(path);if(original.length>8*1024*1024)throw new IOException("Server list unexpectedly large");
            compressed=original.length>2&&(original[0]&255)==31&&(original[1]&255)==139;
            try(DataInputStream in=new DataInputStream(compressed?new GZIPInputStream(new ByteArrayInputStream(original)):new ByteArrayInputStream(original))){if(in.readUnsignedByte()!=10)throw new IOException("Invalid server list");rootName=in.readUTF();root=read(in,10,0);}
        }else root=new Tag(10,new LinkedHashMap<String,Tag>());
        Map<String,Tag> fields=compound(root);Tag servers=fields.get("servers");
        if(servers==null){servers=new Tag(9,new ListTag(10,new ArrayList<>()));fields.put("servers",servers);}
        if(servers.type!=9||((ListTag)servers.value).type!=10)throw new IOException("Invalid server entries");
        List<Tag> entries=((ListTag)servers.value).items;boolean publicFound=false,changed=original==null;
        for(Tag entry:entries){
            Map<String,Tag> values=compound(entry);String ip=string(values.get("ip")),name=string(values.get("name"));
            boolean local=Set.of("localhost","localhost:25565","127.0.0.1","127.0.0.1:25565").contains(ip);
            boolean home=ip.equals("10.0.0.133")||ip.equals("10.0.0.133:25565");
            boolean publicAddress=ip.equals("73.109.48.125")||ip.equals("73.109.48.125:25565")||ip.equals(HOST)||ip.equals(HOST+":25565");
            boolean factory=name.startsWith("Family Server")||name.equals("Family Create and Explore")||name.startsWith("Create and Explore - Family Edition")||name.startsWith("KnowlesCraft");
            if(!publicAddress&&!factory)continue;
            boolean managed=values.containsKey("knowlescraftManaged")||name.equals("KnowlesCraft")&&(publicAddress||home);
            boolean main=managed||!local&&!home;
            String newIp=main?destination:ip,newName=main?"KnowlesCraft":local?"KnowlesCraft (this computer)":"KnowlesCraft (home Wi-Fi)";
            if(main){publicFound=true;if(!values.containsKey("knowlescraftManaged")){values.put("knowlescraftManaged",new Tag(1,(byte)1));changed=true;}}
            if(!newIp.equals(ip)||!newName.equals(name)){values.put("ip",new Tag(8,newIp));values.put("name",new Tag(8,newName));changed=true;}
        }
        if(!publicFound){Map<String,Tag> added=new LinkedHashMap<>();added.put("name",new Tag(8,"KnowlesCraft"));added.put("ip",new Tag(8,destination));added.put("knowlescraftManaged",new Tag(1,(byte)1));entries.add(new Tag(10,added));changed=true;}
        if(changed){
            if(original!=null){Path backup=game.resolve("servers.dat.before-knowlescraft");if(!Files.exists(backup))Files.write(backup,original);}
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();try(DataOutputStream out=new DataOutputStream(compressed?new GZIPOutputStream(bytes):bytes)){out.writeByte(10);out.writeUTF(rootName);write(out,root);}
            Path tmp=game.resolve("servers.dat.knowlescraft.tmp");Files.write(tmp,bytes.toByteArray());Files.move(tmp,path,StandardCopyOption.REPLACE_EXISTING);
        }
        Path instance=game.toAbsolutePath().getParent().resolve("instance.cfg");
        if(Files.isRegularFile(instance)){
            String before=Files.readString(instance);String after=before.replaceAll("(?m)^name=(?:Create and Explore - Family Edition|Family Edition|Family Minecraft)[^\\r\\n]*","name=KnowlesCraft");
            if(!after.equals(before))Files.writeString(instance,after);
        }
    }
    record Tag(int type,Object value){} record ListTag(int type,List<Tag> items){}
    @SuppressWarnings("unchecked") static Map<String,Tag> compound(Tag t){return (Map<String,Tag>)t.value;}
    static String string(Tag t){return t!=null&&t.type==8?(String)t.value:"";}
    static int length(DataInputStream in)throws IOException{int n=in.readInt();if(n<0||n>8*1024*1024)throw new IOException("Invalid NBT length");return n;}
    static Tag read(DataInputStream in,int type,int depth)throws IOException{
        if(depth>64)throw new IOException("NBT too deep");Object value;
        switch(type){
            case 1:value=in.readByte();break;case 2:value=in.readShort();break;case 3:value=in.readInt();break;case 4:value=in.readLong();break;case 5:value=in.readFloat();break;case 6:value=in.readDouble();break;
            case 7:{int n=length(in);byte[] a=in.readNBytes(n);if(a.length!=n)throw new EOFException();value=a;break;}
            case 8:value=in.readUTF();break;
            case 9:{int subtype=in.readUnsignedByte(),n=length(in);if(n>100000)throw new IOException("Too many entries");List<Tag>a=new ArrayList<>();for(int i=0;i<n;i++)a.add(read(in,subtype,depth+1));value=new ListTag(subtype,a);break;}
            case 10:{Map<String,Tag> a=new LinkedHashMap<>();int t;while((t=in.readUnsignedByte())!=0){String key=in.readUTF();a.put(key,read(in,t,depth+1));}value=a;break;}
            case 11:{int n=length(in);int[] a=new int[n];for(int i=0;i<n;i++)a[i]=in.readInt();value=a;break;}
            case 12:{int n=length(in);long[] a=new long[n];for(int i=0;i<n;i++)a[i]=in.readLong();value=a;break;}
            default:throw new IOException("Unsupported NBT tag");
        }return new Tag(type,value);
    }
    static void write(DataOutputStream out,Tag tag)throws IOException{
        switch(tag.type){
            case 1->out.writeByte((Byte)tag.value);case 2->out.writeShort((Short)tag.value);case 3->out.writeInt((Integer)tag.value);case 4->out.writeLong((Long)tag.value);case 5->out.writeFloat((Float)tag.value);case 6->out.writeDouble((Double)tag.value);
            case 7->{byte[] a=(byte[])tag.value;out.writeInt(a.length);out.write(a);}case 8->out.writeUTF((String)tag.value);
            case 9->{ListTag a=(ListTag)tag.value;out.writeByte(a.type);out.writeInt(a.items.size());for(Tag item:a.items)write(out,item);}
            case 10->{for(var entry:compound(tag).entrySet()){out.writeByte(entry.getValue().type);out.writeUTF(entry.getKey());write(out,entry.getValue());}out.writeByte(0);}
            case 11->{int[] a=(int[])tag.value;out.writeInt(a.length);for(int v:a)out.writeInt(v);}case 12->{long[] a=(long[])tag.value;out.writeInt(a.length);for(long v:a)out.writeLong(v);}
            default->throw new IOException("Unsupported NBT tag");
        }
    }
    public static void main(String[] args)throws Exception{if(args[0].equals("voice"))applyVoice(Path.of(args[1]));else migrate(Path.of(args[0]));}
}
