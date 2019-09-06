package fr.fifoube.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import fr.fifoube.blocks.tileentity.TileEntityBlockSeller;
import fr.fifoube.gui.container.ContainerSeller;
import fr.fifoube.main.ModEconomyInc;
import fr.fifoube.packets.PacketSellerCreated;
import fr.fifoube.packets.PacketsRegistery;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class GuiSeller extends ContainerScreen<ContainerSeller> {

	private TileEntityBlockSeller tile;
	
	public GuiSeller(ContainerSeller container, PlayerInventory playerInventory, ITextComponent name) 
	{
		super(container, playerInventory, name);
		this.tile = getContainer().getTileEntity();
	}
	
	private static final ResourceLocation background = new ResourceLocation(ModEconomyInc.MOD_ID ,"textures/gui/container/gui_seller.png");
	protected int xSize = 176;
	protected int ySize = 168;
	protected int guiLeft;
	protected int guiTop;
	
	private Button validate;
	private Button one;
	private Button five;
	private Button ten;
	private Button twenty;
	private Button fifty;
	private Button hundreed;
	private Button twoHundreed;
	private Button fiveHundreed;
	private Button unlimitedStack;
	
	private double cost;
	private boolean admin = false;
	
	
	
	@Override
	public void tick() 
	{
		super.tick();
	}
	
	@Override
	protected void init() {
		super.init();
		PlayerEntity player = minecraft.player;
		if(tile.getCreated() == false)
		{
			this.validate = this.addButton(new Button(width / 2 + 26, height / 2 + 83, 55, 20, I18n.format("title.validate"), actionPerformed()));
			this.one = this.addButton(new Button(width / 2 - 122, height / 2 - 75, 35, 20, "1", actionPerformed()));
			this.five = this.addButton(new Button(width / 2 - 122, height / 2 - 56, 35, 20, "5", actionPerformed()));
			this.ten = this.addButton(new Button(width / 2- 122, height / 2 - 37, 35, 20, "10", actionPerformed()));
			this.twenty = this.addButton(new Button(width / 2 - 122, height / 2 - 18, 35, 20, "20", actionPerformed()));
			this.fifty = this.addButton(new Button(width / 2 - 122, height / 2 + 1, 35, 20, "50", actionPerformed()));
			this.hundreed = this.addButton(new Button(width / 2 - 122, height / 2 + 20, 35, 20, "100", actionPerformed()));
			this.twoHundreed = this.addButton(new Button(width / 2 - 122, height / 2 + 39, 35, 20, "200", actionPerformed()));
			this.fiveHundreed = this.addButton(new Button(width / 2 - 122, height / 2 + 58, 35, 20, "500", actionPerformed()));
			if(player.isCreative() == true)
			{
				this.unlimitedStack = this.addButton(new Button(width /2 + 2, height / 2 - 96, 80, 13, I18n.format("title.unlimited"), actionPerformed()));
			}
		}
	}
	
	
	protected IPressable actionPerformed()
	{
		PlayerEntity playerIn = minecraft.player;
		if(tile != null)
		{
			if(buttons == this.unlimitedStack)
			{
				if(this.admin == false)
				{
					this.admin = true;
					tile.setAdmin(true);
					playerIn.sendMessage(new StringTextComponent(I18n.format("title.unlimitedStack")));
				}
				else if(this.admin == true)
				{
					this.admin = false;
					tile.setAdmin(false);
					playerIn.sendMessage(new StringTextComponent(I18n.format("title.limitedStack")));
				}
			}
			if(buttons == this.one)
			{
				tile.setCost(1);
				this.cost = 1;
			}
			else if(buttons == this.five)
			{
				tile.setCost(5);
				this.cost = 5;
			}
			else if(buttons == this.ten)
			{
				tile.setCost(10);
				this.cost = 10;
			}
			else if(buttons == this.twenty)
			{
				tile.setCost(20);
				this.cost = 20;
			}
			else if(buttons == this.fifty)
			{
				tile.setCost(50);
				this.cost = 50;
			}
			else if(buttons == this.hundreed)
			{
				tile.setCost(100);
				this.cost = 100;
			}
			else if(buttons == this.twoHundreed)
			{
				tile.setCost(200);
				this.cost = 200;
			}
			else if(buttons == this.fiveHundreed)
			{
				tile.setCost(500);
				this.cost = 500;
			}
			else if(buttons == this.validate)
			{
				if(!(tile.getCost() == 0)) // IF TILE HAS NOT A COST OF 0 THEN WE PASS TO THE OTHER
				{
					if(tile.getStackInSlot(0) != ItemStack.EMPTY) // IF SLOT 0 IS NOT BLOCKS.AIR, WE PASS
					{
						if(!this.admin) //ADMIN HASN'T SET : UNLIMITED STACK
						{
							tile.setAdmin(false);
							tile.setCreated(true); // CLIENT SET CREATED AT TRUE
							final int x = tile.getPos().getX(); // GET X
							final int y = tile.getPos().getY(); // GET Y
							final int z = tile.getPos().getZ(); // GET Z
							int amount = tile.getStackInSlot(0).getCount(); // GET COUNT IN TILE THANKS TO STACK IN SLOT
							String name = tile.getStackInSlot(0).getDisplayName().getString(); // GET ITEM NAME IN TILE THANKS TO STACK IN SLOT
							tile.setAmount(amount); //CLIENT SET AMOUNT
							tile.setItem(name); // CLIENT SET ITEM NAME
							tile.markDirty();
							PacketsRegistery.CHANNEL.sendToServer(new PacketSellerCreated(true, this.cost, name, amount, x, y, z, false)); // SEND SERVER PACKET FOR CREATED, COST, NAME, AMOUNT, X,Y,Z ARE TILE COORDINATES
							playerIn.closeScreen(); // CLOSE SCREEN
						}
						else if(this.admin) //ADMIN HAS SET : UNLIMITED STACK
						{
							tile.setAdmin(true);
							tile.setCreated(true); // CLIENT SET CREATED AT TRUE
							final int x = tile.getPos().getX(); // GET X
							final int y = tile.getPos().getY(); // GET Y
							final int z = tile.getPos().getZ(); // GET Z
							int amount = tile.getStackInSlot(0).getCount(); // GET COUNT IN TILE THANKS TO STACK IN SLOT
							String name = tile.getStackInSlot(0).getDisplayName().getString(); // GET ITEM NAME IN TILE THANKS TO STACK IN SLOT
							tile.setAmount(amount); //CLIENT SET AMOUNT
							tile.setItem(name); // CLIENT SET ITEM NAME
							tile.markDirty();
							PacketsRegistery.CHANNEL.sendToServer(new PacketSellerCreated(true, this.cost, name, amount, x, y, z, true)); // SEND SERVER PACKET FOR CREATED, COST, NAME, AMOUNT, X,Y,Z ARE TILE COORDINATES
							playerIn.closeScreen(); // CLOSE SCREEN
						}
						
					}
					else // PROVIDE PLAYER TO SELL AIR
					{
						playerIn.sendMessage(new StringTextComponent(I18n.format("title.sellAir")));	
					}
				}
				else // IT MEANS THAT PLAYER HAS NOT SELECTED A COST
				{
					playerIn.sendMessage(new StringTextComponent(I18n.format("title.noCost")));
				}
			}
		}
		return new IPressable() {
			
			@Override
			public void onPress(Button button) {
				
			}
		};
		
	}
	
	@Override
		public boolean isPauseScreen() {
			return false;
		}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) 
	{
		this.drawString(font, new TranslationTextComponent(I18n.format("title.block_seller")).getFormattedText(), 8, 5, Color.DARK_GRAY.getRGB());
		this.drawString(font, new TranslationTextComponent("Inventory").getFormattedText(), 8, 73, Color.DARK_GRAY.getRGB());
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) 
	{
	       GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F); 
	       this.minecraft.getTextureManager().bindTexture(background); 
	       int k = (this.width - this.xSize) / 2; 
	       int l = (this.height - this.ySize) / 2;
	       this.blit(k, l, 0, 0, this.xSize, this.ySize); 
	}
}
