package tests.examples;
import java.util.ArrayList;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import mcnet.components.Checker;
import mcnet.components.NetContext;
import mcnet.components.Network;
import mcnet.components.NetworkObject;
import mcnet.components.Tuple;
import mcnet.netobjs.AclFirewall;
import mcnet.netobjs.EndHost;
import mcnet.netobjs.EndHost;
import mcnet.netobjs.PolitoNat;
public class Test_1{
    public Checker check;
    public AclFirewall firewall;
    public EndHost webserver;
    public EndHost user2;
    public PolitoNat nat;
    public Test_1(Context ctx){
        NetContext nctx = new NetContext (ctx,new String[]{"firewall", "webserver", "user2", "nat"}, new String[]{"ip_firewall", "ip_webserver", "ip_user2", "ip_nat"});
        Network net = new Network (ctx,new Object[]{nctx});
        firewall = new AclFirewall(ctx, new Object[]{nctx.nm.get("firewall"), net, nctx});
        webserver = new EndHost(ctx, new Object[]{nctx.nm.get("webserver"), net, nctx});
        user2 = new EndHost(ctx, new Object[]{nctx.nm.get("user2"), net, nctx});
        nat = new PolitoNat(ctx, new Object[]{nctx.nm.get("nat"), net, nctx});
        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al0 = new ArrayList<DatatypeExpr>();
        al0.add(nctx.am.get("ip_firewall"));
        adm.add(new Tuple<>((NetworkObject)firewall, al0));
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_webserver"));
        adm.add(new Tuple<>((NetworkObject)webserver, al1));
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        al2.add(nctx.am.get("ip_user2"));
        adm.add(new Tuple<>((NetworkObject)user2, al2));
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        al3.add(nctx.am.get("ip_nat"));
        adm.add(new Tuple<>((NetworkObject)nat, al3));
        net.setAddressMappings(adm);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_firewall = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), nat));
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), webserver));
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_user2"), nat));
        net.routingTable(firewall, rt_firewall);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_webserver = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), firewall));
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_user2"), firewall));
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), firewall));
        net.routingTable(webserver, rt_webserver);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_user2 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_user2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), nat));
        rt_user2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), nat));
        rt_user2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), nat));
        net.routingTable(user2, rt_user2);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), firewall));
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), firewall));
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_user2"), user2));
        net.routingTable(nat, rt_nat);
        net.attach(firewall, webserver, user2, nat);
        ArrayList<DatatypeExpr> ia = new ArrayList<DatatypeExpr>();
        ia.add(nctx.am.get("ip_user2"));
//        nat.setInternalAddress(ia);
        check = new Checker(ctx,nctx,net);
    }
}

