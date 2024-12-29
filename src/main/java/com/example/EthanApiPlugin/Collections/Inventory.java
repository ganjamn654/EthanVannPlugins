package com.example.EthanApiPlugin.Collections;

import com.example.EthanApiPlugin.Collections.query.ItemQuery;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Inventory {
    static Client client = RuneLite.getInjector().getInstance(Client.class);
    static List<Widget> inventoryItems = new ArrayList<>();
    static int lastUpdateTick = 0;

    public static ItemQuery search() {
        if (lastUpdateTick < client.getTickCount()) {
            client.runScript(6009, 9764864, 28, 1, -1);
            Inventory.inventoryItems =
                    Arrays.stream(client.getWidget(WidgetInfo.INVENTORY).getDynamicChildren()).filter(Objects::nonNull).filter(x -> x.getItemId() != 6512 && x.getItemId() != -1).collect(Collectors.toList());
            lastUpdateTick = client.getTickCount();
        }
        return new ItemQuery(inventoryItems);
    }

    public static int getEmptySlots() {
        return 28 - search().result().size();
    }

    public static boolean full() {
        return getEmptySlots() == 0;
    }

    public static int getItemAmount(int itemId) {
        return search().withId(itemId).result().size();
    }

    public static int getItemAmount(String itemName) {
        return search().withName(itemName).result().size();
    }

    public static int getItemQuantity(String itemName) {
        return search().withName(itemName).result().stream().mapToInt(Widget::getItemQuantity).sum();
    }

    public static int getItemQuantity(int itemId) {
        return search().withId(itemId).result().stream().mapToInt(Widget::getItemQuantity).sum();
    }

    public static boolean withdrawItemFromBank(String itemName, int quantity) {
        if (Bank.isOpen()) {
            int currentQuantity = getItemQuantity(itemName);
            if (currentQuantity < quantity) {
                // Logic to interact with the bank and withdraw the item
                System.out.println("Withdrawing " + (quantity - currentQuantity) + " " + itemName + " from bank.");
                // Placeholder for bank interaction logic
                return true;
            }
        }
        return false;
    }

    public static boolean withdrawItemFromBank(int itemId, int quantity) {
        if (Bank.isOpen()) {
            int currentQuantity = getItemQuantity(itemId);
            if (currentQuantity < quantity) {
                // Logic to interact with the bank and withdraw the item
                System.out.println("Withdrawing " + (quantity - currentQuantity) + " of item ID " + itemId + " from bank.");
                // Placeholder for bank interaction logic
                return true;
            }
        }
        return false;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.HOPPING || gameStateChanged.getGameState() == GameState.LOGIN_SCREEN || gameStateChanged.getGameState() == GameState.CONNECTION_LOST) {
            Inventory.inventoryItems.clear();
        }
    }
}