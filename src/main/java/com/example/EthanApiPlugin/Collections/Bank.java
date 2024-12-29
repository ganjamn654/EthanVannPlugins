package com.example.EthanApiPlugin.Collections;

import com.example.EthanApiPlugin.Collections.query.ItemQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Bank {
    static Client client = RuneLite.getInjector().getInstance(Client.class);
    static List<Widget> bankItems = new ArrayList<>();
    boolean bankUpdate = true;
    static int lastUpdateTick = 0;

    public static ItemQuery search() {
        if (lastUpdateTick < client.getTickCount()) {
            Bank.bankItems.clear();
            int i = 0;
            if(client.getItemContainer(InventoryID.BANK)==null){
                return new ItemQuery(new ArrayList<>());
            }
            for (Item item : client.getItemContainer(InventoryID.BANK).getItems()) {
                try {
                    if (item == null) {
                        i++;
                        continue;
                    }
                    if (EthanApiPlugin.itemDefs.get(item.getId()).getPlaceholderTemplateId() == 14401) {
                        i++;
                        continue;
                    }
                    Bank.bankItems.add(new BankItemWidget(EthanApiPlugin.itemDefs.get(item.getId()).getName(), item.getId(), item.getQuantity(), i));
                } catch (NullPointerException | ExecutionException ex) {
                    //todo fix this
                }
                i++;
            }
            lastUpdateTick = client.getTickCount();
        }
        return new ItemQuery(bankItems.stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public static boolean isOpen() {
        return client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER) != null && !client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER).isHidden();
    }


    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.HOPPING || gameStateChanged.getGameState() == GameState.LOGIN_SCREEN || gameStateChanged.getGameState() == GameState.CONNECTION_LOST) {
            Bank.bankItems.clear();
        }
    }

    public static boolean withdrawItem(String itemName, int quantity) {
        if (!isOpen()) {
            System.out.println("Bank is not open. Cannot withdraw items.");
            return false;
        }

        ItemQuery query = search().withName(itemName);
        if (query.empty()) {
            System.out.println("Item not found in bank: " + itemName);
            return false;
        }

        Widget itemWidget = query.result().get(0);
        int currentQuantity = Inventory.getItemQuantity(itemName);
        if (currentQuantity >= quantity) {
            System.out.println("Already have enough " + itemName + " in inventory.");
            return true;
        }

        int withdrawQuantity = quantity - currentQuantity;
        System.out.println("Withdrawing " + withdrawQuantity + " " + itemName + " from bank.");
        // Placeholder for actual withdrawal logic
        return true;
    }

    public static boolean withdrawItem(int itemId, int quantity) {
        if (!isOpen()) {
            System.out.println("Bank is not open. Cannot withdraw items.");
            return false;
        }

        ItemQuery query = search().withId(itemId);
        if (query.empty()) {
            System.out.println("Item not found in bank with ID: " + itemId);
            return false;
        }

        Widget itemWidget = query.result().get(0);
        int currentQuantity = Inventory.getItemQuantity(itemId);
        if (currentQuantity >= quantity) {
            System.out.println("Already have enough of item ID " + itemId + " in inventory.");
            return true;
        }

        int withdrawQuantity = quantity - currentQuantity;
        System.out.println("Withdrawing " + withdrawQuantity + " of item ID " + itemId + " from bank.");
        // Placeholder for actual withdrawal logic
        return true;
    }
}