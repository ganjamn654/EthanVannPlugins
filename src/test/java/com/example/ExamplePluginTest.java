package com.example;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.testing.TestingPlugin;
import com.example.EthanApiPlugin.Plugins.AutoRevenantKillerPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(EthanApiPlugin.class, PacketUtilsPlugin.class, TestingPlugin.class, AutoRevenantKillerPlugin.class);
        RuneLite.main(args);
    }
}