package hu.gpsoft.device.programs;

import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.*;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.*;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.SlideListener;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.object.Canvas;
import com.mrcrayfish.device.object.ColorGrid;
import com.mrcrayfish.device.object.Picture;
import com.mrcrayfish.device.object.Picture.Size;
import com.mrcrayfish.device.programs.system.layout.StandardLayout;
import hu.gpsoft.device.Reference;
import hu.gpsoft.device.api.app.Application;
import hu.gpsoft.device.api.app.Component;
import hu.gpsoft.device.api.app.Icons;
import hu.gpsoft.device.api.app.Layout;
import hu.gpsoft.device.api.app.component.*;
import hu.gpsoft.device.api.app.listener.SlideListener;
import hu.gpsoft.device.api.app.renderer.ListItemRenderer;
import hu.gpsoft.device.api.io.File;
import hu.gpsoft.device.api.print.IPrint;
import hu.gpsoft.device.api.utils.RenderUtil;
import hu.gpsoft.device.core.Laptop;
import hu.gpsoft.device.core.io.FileSystem;
import hu.gpsoft.device.programs.system.layout.StandardLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.Button;

public class ApplicationPixelPainter extends Application
{
	private static final ResourceLocation PIXEL_PAINTER_ICONS = new ResourceLocation("cdm:textures/gui/pixel_painter.png");

	private static final Color ITEM_BACKGROUND = new Color(170, 176, 194);
	private static final Color ITEM_SELECTED = new Color(200, 176, 174);
	private static final Color AUTHOR_TEXT = new Color(114, 120, 138);

	/* Main Menu */
	private StandardLayout layoutMainMenu;
	private hu.gpsoft.device.api.app.component.Label labelLogo;
	private hu.gpsoft.device.api.app.component.Button btnNewPicture;
	private hu.gpsoft.device.api.app.component.Button btnLoadPicture;

	/* New Picture */
	private Layout layoutNewPicture;
	private hu.gpsoft.device.api.app.component.Label labelName;
	private hu.gpsoft.device.api.app.component.TextField fieldName;
	private hu.gpsoft.device.api.app.component.Label labelAuthor;
	private hu.gpsoft.device.api.app.component.TextField fieldAuthor;
	private hu.gpsoft.device.api.app.component.Label labelSize;
	private CheckBox checkBox16x;
	private CheckBox checkBox32x;
	private hu.gpsoft.device.api.app.component.Button btnCreatePicture;

	/* Load Picture */
	private Layout layoutLoadPicture;
	private ItemList<Picture> listPictures;
	private hu.gpsoft.device.api.app.component.Button btnLoadSavedPicture;
	private hu.gpsoft.device.api.app.component.Button btnBrowseSavedPicture;
	private hu.gpsoft.device.api.app.component.Button btnDeleteSavedPicture;
	private hu.gpsoft.device.api.app.component.Button btnBackSavedPicture;

	/* Drawing */
	private Layout layoutDraw;
	private Canvas canvas;
	private ButtonToggle btnPencil;
	private ButtonToggle btnBucket;
	private ButtonToggle btnEraser;
	private ButtonToggle btnEyeDropper;
	private hu.gpsoft.device.api.app.component.Button btnCancel;
	private hu.gpsoft.device.api.app.component.Button btnSave;
	private Slider redSlider;
	private Slider greenSlider;
	private Slider blueSlider;
	private hu.gpsoft.device.api.app.Component colorDisplay;
	private ColorGrid colorGrid;
	private CheckBox displayGrid;

	public ApplicationPixelPainter()
	{
		//super("pixel_painter", "Pixel Painter");
	}

	@Override
	public void init(@Nullable NBTTagCompound intent)
	{
		/* Main Menu */
		layoutMainMenu = new StandardLayout("Main Menu", 201, 125, this, null);
		layoutMainMenu.setIcon(Icons.HOME);

		ItemList<Picture> pictureList = new ItemList<>(5, 43, 80, 4);
		pictureList.setListItemRenderer(new ListItemRenderer<Picture>(18)
		{
			@Override
			public void render(Picture picture, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected)
			{
				RenderUtil.drawStringClipped("Henlo", x, y, 100, AUTHOR_TEXT.getRGB(), true);
			}
		});
		layoutMainMenu.addComponent(pictureList);

		btnNewPicture = new hu.gpsoft.device.api.app.component.Button(5, 25, "New", Icons.PICTURE);
		btnNewPicture.setSize(40, 16);
		btnNewPicture.setToolTip("New Picture", "Start a new masterpiece!");
		btnNewPicture.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			if(mouseButton == 0)
			{
				setCurrentLayout(layoutNewPicture);
			}
		});
		layoutMainMenu.addComponent(btnNewPicture);

		btnLoadPicture = new hu.gpsoft.device.api.app.component.Button(48, 25, Icons.IMPORT);
		btnLoadPicture.setToolTip("Load External", "Open a picture from file");
		btnLoadPicture.setClickListener((mouseX, mouseY, mouseButton) -> setCurrentLayout(layoutLoadPicture));
		layoutMainMenu.addComponent(btnLoadPicture);

		hu.gpsoft.device.api.app.component.Button btnDeletePicture = new hu.gpsoft.device.api.app.component.Button(67, 25, Icons.TRASH);
		btnDeletePicture.setToolTip("Delete", "Removes the selected image");
		layoutMainMenu.addComponent(btnDeletePicture);

		
		/* New Picture */
		
		layoutNewPicture = new Layout(180, 65);

		labelName = new hu.gpsoft.device.api.app.component.Label("Name", 5, 5);
		layoutNewPicture.addComponent(labelName);

		fieldName = new hu.gpsoft.device.api.app.component.TextField(5, 15, 100);
		layoutNewPicture.addComponent(fieldName);

		labelAuthor = new hu.gpsoft.device.api.app.component.Label("Author", 5, 35);
		layoutNewPicture.addComponent(labelAuthor);

		fieldAuthor = new hu.gpsoft.device.api.app.component.TextField(5, 45, 100);
		layoutNewPicture.addComponent(fieldAuthor);

		labelSize = new hu.gpsoft.device.api.app.component.Label("Size", 110, 5);
		layoutNewPicture.addComponent(labelSize);

		RadioGroup sizeGroup = new RadioGroup();

		checkBox16x = new CheckBox("16x", 110, 17);
		checkBox16x.setSelected(true);
		checkBox16x.setRadioGroup(sizeGroup);
		layoutNewPicture.addComponent(checkBox16x);

		checkBox32x = new CheckBox("32x", 145, 17);
		checkBox32x.setRadioGroup(sizeGroup);
		layoutNewPicture.addComponent(checkBox32x);

		btnCreatePicture = new hu.gpsoft.device.api.app.component.Button(110, 40, "Create");
		btnCreatePicture.setSize(65, 20);
		btnCreatePicture.setClickListener((mouseX, mouseY, mouseButton) ->
		{
            setCurrentLayout(layoutDraw);
            canvas.createPicture(fieldName.getText(), fieldAuthor.getText(), checkBox16x.isSelected() ? Size.X16 : Size.X32);
        });
		layoutNewPicture.addComponent(btnCreatePicture);

		
		/* Load Picture */
		
		layoutLoadPicture = new Layout(165, 116);
		layoutLoadPicture.setInitListener(() ->
		{
			listPictures.removeAll();
			FileSystem.getApplicationFolder(this, (folder, success) ->
			{
                if(success)
				{
					folder.search(file -> file.isForApplication(this)).forEach(file ->
					{
						Picture picture = Picture.fromFile(file);
						listPictures.addItem(picture);
					});
				}
            });
        });

		listPictures = new ItemList<>(5, 5, 80, 5);
		listPictures.setListItemRenderer(new ListItemRenderer<Picture>(20)
		{
			@Override
			public void render(Picture picture, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected)
			{
				Gui.drawRect(x, y, x + width, y + height, selected ? ITEM_SELECTED.getRGB() : ITEM_BACKGROUND.getRGB());
				mc.fontRenderer.drawString(picture.getName(), x + 2, y + 2, Color.WHITE.getRGB(), false);
				mc.fontRenderer.drawString(picture.getAuthor(), x + 2, y + 11, AUTHOR_TEXT.getRGB(), false);
			}
		});
		listPictures.setItemClickListener((picture, index, mouseButton) ->
		{
            if(mouseButton == 0)
			{
				btnLoadSavedPicture.setEnabled(true);
				btnDeleteSavedPicture.setEnabled(true);
			}
        });
		layoutLoadPicture.addComponent(listPictures);

		btnLoadSavedPicture = new hu.gpsoft.device.api.app.component.Button(110, 5, "Load");
		btnLoadSavedPicture.setSize(50, 20);
		btnLoadSavedPicture.setEnabled(false);
		btnLoadSavedPicture.setClickListener((mouseX, mouseY, mouseButton) ->
		{
            if (listPictures.getSelectedIndex() != -1)
            {
                canvas.setPicture(listPictures.getSelectedItem());
                setCurrentLayout(layoutDraw);
            }
        });
		layoutLoadPicture.addComponent(btnLoadSavedPicture);

		btnBrowseSavedPicture = new hu.gpsoft.device.api.app.component.Button(110, 30, "Browse");
		btnBrowseSavedPicture.setSize(50, 20);
		btnBrowseSavedPicture.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			hu.gpsoft.device.api.app.Dialog.OpenFile dialog = new hu.gpsoft.device.api.app.Dialog.OpenFile(this);
			dialog.setResponseHandler((success, file) ->
			{
				if(file.isForApplication(this))
				{
					Picture picture = Picture.fromFile(file);
					canvas.setPicture(picture);
					setCurrentLayout(layoutDraw);
					return true;
				}
				else
				{
					hu.gpsoft.device.api.app.Dialog.Message dialog2 = new hu.gpsoft.device.api.app.Dialog.Message("Invalid file for Pixel Painter");
					openDialog(dialog2);
				}
				return false;
			});
			openDialog(dialog);
        });
		layoutLoadPicture.addComponent(btnBrowseSavedPicture);

		btnDeleteSavedPicture = new hu.gpsoft.device.api.app.component.Button(110, 55, "Delete");
		btnDeleteSavedPicture.setSize(50, 20);
		btnDeleteSavedPicture.setEnabled(false);
		btnDeleteSavedPicture.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			if(listPictures.getSelectedIndex() != -1)
			{
				Picture picture = listPictures.getSelectedItem();
				File file = picture.getSource();
				if(file != null)
				{
					file.delete((o, success) ->
					{
						if(success)
						{
							listPictures.removeItem(listPictures.getSelectedIndex());
							btnDeleteSavedPicture.setEnabled(false);
							btnLoadSavedPicture.setEnabled(false);
						}
						else
						{
							//TODO error dialog
						}
                    });
				}
				else
				{
					//TODO error dialog
				}
			}
		});
		layoutLoadPicture.addComponent(btnDeleteSavedPicture);

		btnBackSavedPicture = new hu.gpsoft.device.api.app.component.Button(110, 80, "Back");
		btnBackSavedPicture.setSize(50, 20);
		btnBackSavedPicture.setClickListener((mouseX, mouseY, mouseButton) -> setCurrentLayout(layoutMainMenu));
		layoutLoadPicture.addComponent(btnBackSavedPicture);

		
		/* Drawing */
		
		layoutDraw = new Layout(213, 140);

		canvas = new Canvas(5, 5);
		layoutDraw.addComponent(canvas);

		RadioGroup toolGroup = new RadioGroup();

		btnPencil = new ButtonToggle(138, 5, PIXEL_PAINTER_ICONS, 0, 0, 10, 10);
		btnPencil.setClickListener((mouseX, mouseY, mouseButton) -> canvas.setCurrentTool(Canvas.PENCIL));
		btnPencil.setRadioGroup(toolGroup);
		layoutDraw.addComponent(btnPencil);

		btnBucket = new ButtonToggle(138, 24, PIXEL_PAINTER_ICONS, 10, 0, 10, 10);
		btnBucket.setClickListener((mouseX, mouseY, mouseButton) -> canvas.setCurrentTool(Canvas.BUCKET));
		btnBucket.setRadioGroup(toolGroup);
		layoutDraw.addComponent(btnBucket);

		btnEraser = new ButtonToggle(138, 43, PIXEL_PAINTER_ICONS, 20, 0, 10, 10);
		btnEraser.setClickListener((mouseX, mouseY, mouseButton) -> canvas.setCurrentTool(Canvas.ERASER));
		btnEraser.setRadioGroup(toolGroup);
		layoutDraw.addComponent(btnEraser);

		btnEyeDropper = new ButtonToggle(138, 62, PIXEL_PAINTER_ICONS, 30, 0, 10, 10);
		btnEyeDropper.setClickListener((mouseX, mouseY, mouseButton) ->
		{
            canvas.setCurrentTool(Canvas.EYE_DROPPER);
			Color color = new Color(canvas.getCurrentColor());
            redSlider.setPercentage(color.getRed() / 255F);
            greenSlider.setPercentage(color.getGreen() / 255F);
            blueSlider.setPercentage(color.getBlue() / 255F);
        });
		btnEyeDropper.setRadioGroup(toolGroup);
		layoutDraw.addComponent(btnEyeDropper);

		hu.gpsoft.device.api.app.component.Button button = new hu.gpsoft.device.api.app.component.Button(138, 81, Icons.PRINTER);
		button.setClickListener((mouseX, mouseY, mouseButton) ->
		{
            if(mouseButton == 0)
			{
				hu.gpsoft.device.api.app.Dialog.Print dialog = new hu.gpsoft.device.api.app.Dialog.Print(new PicturePrint(canvas.picture.getName(), canvas.getPixels(), canvas.picture.getWidth()));
				openDialog(dialog);
			}
        });
		layoutDraw.addComponent(button);

		btnCancel = new hu.gpsoft.device.api.app.component.Button(138, 100, PIXEL_PAINTER_ICONS, 50, 0, 10, 10);
		btnCancel.setClickListener((mouseX, mouseY, mouseButton) ->
		{
            if (canvas.isExistingImage())
                setCurrentLayout(layoutLoadPicture);
            else
                setCurrentLayout(layoutMainMenu);
            canvas.clear();
        });
		layoutDraw.addComponent(btnCancel);

		btnSave = new hu.gpsoft.device.api.app.component.Button(138, 119, PIXEL_PAINTER_ICONS, 40, 0, 10, 10);
		btnSave.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			canvas.picture.pixels = canvas.copyPixels();

			NBTTagCompound pictureTag = new NBTTagCompound();
			canvas.picture.writeToNBT(pictureTag);

			if(canvas.isExistingImage())
			{
				File file = canvas.picture.getSource();
				if(file != null)
				{
					file.setData(pictureTag, (response, success) ->
					{
						if(response.getStatus() == FileSystem.Status.SUCCESSFUL)
						{
							canvas.clear();
							setCurrentLayout(layoutLoadPicture);
						}
						else
						{
							//TODO error dialog
						}
					});
				}
			}
			else
			{
				hu.gpsoft.device.api.app.Dialog.SaveFile dialog = new hu.gpsoft.device.api.app.Dialog.SaveFile(ApplicationPixelPainter.this, pictureTag);
				dialog.setResponseHandler((success, file) ->
				{
					if(success)
					{
						canvas.clear();
						setCurrentLayout(layoutLoadPicture);
						return true;
					}
					else
					{
						//TODO error dialog
					}
					return false;
				});
				openDialog(dialog);
			}
		});
		layoutDraw.addComponent(btnSave);

		redSlider = new Slider(158, 30, 50);
		redSlider.setSlideListener(new SlideListener()
		{
			@Override
			public void onSlide(float percentage)
			{
				canvas.setRed(percentage);
			}
		});
		layoutDraw.addComponent(redSlider);

		greenSlider = new Slider(158, 46, 50);
		greenSlider.setSlideListener(new SlideListener()
		{
			@Override
			public void onSlide(float percentage)
			{
				canvas.setGreen(percentage);
			}
		});
		layoutDraw.addComponent(greenSlider);

		blueSlider = new Slider(158, 62, 50);
		blueSlider.setSlideListener(new SlideListener()
		{
			@Override
			public void onSlide(float percentage)
			{
				canvas.setBlue(percentage);
			}
		});
		layoutDraw.addComponent(blueSlider);

		colorDisplay = new Component(158, 5)
		{
			@Override
			public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
			{
				drawRect(xPosition, yPosition, xPosition + 50, yPosition + 20, Color.DARK_GRAY.getRGB());
				drawRect(xPosition + 1, yPosition + 1, xPosition + 49, yPosition + 19, canvas.getCurrentColor());
			}
		};
		layoutDraw.addComponent(colorDisplay);

		colorGrid = new ColorGrid(157, 82, 50, canvas, redSlider, greenSlider, blueSlider);
		layoutDraw.addComponent(colorGrid);

		displayGrid = new CheckBox("Grid", 166, 120);
		displayGrid.setClickListener((mouseX, mouseY, mouseButton) -> canvas.setShowGrid(displayGrid.isSelected()));
		layoutDraw.addComponent(displayGrid);

		setCurrentLayout(layoutMainMenu);
	}

	@Override
	public void load(NBTTagCompound tagCompound)
	{

	}

	@Override
	public void save(NBTTagCompound tagCompound)
	{

	}

	@Override
	public void onClose()
	{
		super.onClose();
		listPictures.removeAll();
	}

	public static class PicturePrint implements IPrint
	{
		private String name;
		private int[] pixels;
		private int resolution;
		private boolean cut;

		public PicturePrint() {}

		public PicturePrint(String name, int[] pixels, int resolution)
		{
			this.name = name;
			this.setPicture(pixels);
		}

		private void setPicture(int[] pixels)
		{
			int resolution = (int) Math.sqrt(pixels.length);
			Size size = Size.getFromSize(resolution);
			if(size == null)
			{
				throw new IllegalArgumentException("Invalid pixels");
			}
			this.resolution = resolution;
			this.pixels = pixels;
		}

		@Override
		public String getName()
		{
			return name;
		}

		@Override
		public int speed()
		{
			return resolution;
		}

		@Override
		public boolean requiresColor()
		{
			for(int pixel : pixels)
			{
				int r = (pixel >> 16 & 255);
				int g = (pixel >> 8 & 255);
				int b = (pixel & 255);
				if(r != g || r != b)
				{
					return true;
				}
			}
			return false;
		}

		@Override
		public NBTTagCompound toTag()
		{
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("name", name);
			tag.setIntArray("pixels", pixels);
			tag.setInteger("resolution", resolution);
			if(cut) tag.setBoolean("cut", cut);
			return tag;
		}

		@Override
		public void fromTag(NBTTagCompound tag)
		{
			name = tag.getString("name");
			cut = tag.getBoolean("cut");
			setPicture(tag.getIntArray("pixels"));
		}

		@Override
		public Class<? extends IPrint.Renderer> getRenderer()
		{
			return PictureRenderer.class;
		}
	}

	public static class PictureRenderer implements IPrint.Renderer
	{
		public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/model/paper.png");

		@Override
		public boolean render(NBTTagCompound data)
		{
			if(data.hasKey("pixels", Constants.NBT.TAG_INT_ARRAY) && data.hasKey("resolution", Constants.NBT.TAG_INT))
			{
				int[] pixels = data.getIntArray("pixels");
				int resolution = data.getInteger("resolution");
				boolean cut = data.getBoolean("cut");

				if(pixels.length != resolution * resolution)
					return false;

				GlStateManager.enableBlend();
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				GlStateManager.disableLighting();
				GlStateManager.rotate(180, 0, 1, 0);

				// This is for the paper background
				if (!cut)
				{
					Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
					RenderUtil.drawRectWithTexture(-1, 0, 0, 0, 1, 1, resolution ,resolution, resolution, resolution);
				}

				// This creates an flipped copy of the pixel array
				// as it otherwise would be mirrored
				int[] flippedPixels = new int[pixels.length];
				for (int i = 0; i < resolution; i++)
				{
					for (int j = 0; j < resolution; j++)
					{
						flippedPixels[resolution - i - 1 + (resolution - j - 1) * resolution] = pixels[i + j * resolution];
					}
				}

				int textureId = TextureUtil.glGenTextures();
				TextureUtil.allocateTexture(textureId, resolution, resolution);
				TextureUtil.uploadTexture(textureId, flippedPixels, resolution, resolution);

				GlStateManager.bindTexture(textureId);
				RenderUtil.drawRectWithTexture(-1, 0, 0, 0, 1, 1, resolution, resolution, resolution, resolution);
				GlStateManager.deleteTexture(textureId);

				GlStateManager.disableRescaleNormal();
				GlStateManager.disableBlend();
				GlStateManager.enableLighting();
				return true;
			}
			return false;
		}
	}
}
