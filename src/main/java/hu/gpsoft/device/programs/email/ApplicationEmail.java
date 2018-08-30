package hu.gpsoft.device.programs.email;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.*;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.component.*;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.TextArea;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.InitListener;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.email.object.Contact;
import com.mrcrayfish.device.programs.email.object.Email;
import com.mrcrayfish.device.programs.email.task.*;
import hu.gpsoft.device.api.ApplicationManager;
import hu.gpsoft.device.api.app.Application;
import hu.gpsoft.device.api.app.Component;
import hu.gpsoft.device.api.app.Icons;
import hu.gpsoft.device.api.app.Layout;
import hu.gpsoft.device.api.app.component.ItemList;
import hu.gpsoft.device.api.app.component.Label;
import hu.gpsoft.device.api.app.component.Spinner;
import hu.gpsoft.device.api.app.component.Text;
import hu.gpsoft.device.api.app.listener.InitListener;
import hu.gpsoft.device.api.app.renderer.ListItemRenderer;
import hu.gpsoft.device.api.io.File;
import hu.gpsoft.device.api.task.TaskManager;
import hu.gpsoft.device.api.utils.RenderUtil;
import hu.gpsoft.device.core.Laptop;
import hu.gpsoft.device.programs.email.object.Contact;
import hu.gpsoft.device.programs.email.object.Email;
import hu.gpsoft.device.programs.email.task.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationEmail extends Application
{
	private static final ResourceLocation ENDER_MAIL_ICONS = new ResourceLocation("cdm:textures/gui/ender_mail.png");
	private static final ResourceLocation ENDER_MAIL_BACKGROUND = new ResourceLocation("cdm:textures/gui/ender_mail_background.png");

	private static final Pattern EMAIL = Pattern.compile("^([a-zA-Z0-9]{1,10})@endermail\\.com$");
	private final Color COLOR_EMAIL_CONTENT_BACKGROUND = new Color(160, 160, 160);

	/* Loading Layout */
	private Layout layoutInit;
	private Spinner spinnerInit;
	private hu.gpsoft.device.api.app.component.Label labelLoading;

	/* Main Menu Layout */
	private Layout layoutMainMenu;
	private hu.gpsoft.device.api.app.component.Image logo;
	private hu.gpsoft.device.api.app.component.Label labelLogo;
	private hu.gpsoft.device.api.app.component.Button btnRegisterAccount;

	/* Register Account Layout */
	private Layout layoutRegisterAccount;
	private hu.gpsoft.device.api.app.component.Label labelEmail;
	private hu.gpsoft.device.api.app.component.TextField fieldEmail;
	private hu.gpsoft.device.api.app.component.Label labelDomain;
	private hu.gpsoft.device.api.app.component.Button btnRegister;

	/* Inbox Layout */
	private Layout layoutInbox;
	private ItemList<Email> listEmails;
	private hu.gpsoft.device.api.app.component.Button btnViewEmail;
	private hu.gpsoft.device.api.app.component.Button btnNewEmail;
	private hu.gpsoft.device.api.app.component.Button btnReplyEmail;
	private hu.gpsoft.device.api.app.component.Button btnDeleteEmail;
	private hu.gpsoft.device.api.app.component.Button btnRefresh;

	/* New Email Layout */
	private Layout layoutNewEmail;
	private hu.gpsoft.device.api.app.component.TextField fieldRecipient;
	private hu.gpsoft.device.api.app.component.TextField fieldSubject;
	private hu.gpsoft.device.api.app.component.TextArea textAreaMessage;
	private hu.gpsoft.device.api.app.component.Button btnSendEmail;
	private hu.gpsoft.device.api.app.component.Button btnCancelEmail;
	private hu.gpsoft.device.api.app.component.Button btnAttachedFile;
	private hu.gpsoft.device.api.app.component.Button btnRemoveAttachedFile;
	private hu.gpsoft.device.api.app.component.Label labelAttachedFile;

	/* View Email Layout */
	private Layout layoutViewEmail;
	private hu.gpsoft.device.api.app.component.Label labelViewSubject;
	private hu.gpsoft.device.api.app.component.Label labelSender;
	private hu.gpsoft.device.api.app.component.Label labelFrom;
	private hu.gpsoft.device.api.app.component.Label labelViewSubjectContent;
	private hu.gpsoft.device.api.app.component.Label labelViewMessage;
	private Text textMessage;
	private hu.gpsoft.device.api.app.component.Button btnCancelViewEmail;
	private hu.gpsoft.device.api.app.component.Button btnSaveAttachment;
	private hu.gpsoft.device.api.app.component.Label labelAttachmentName;
	
	/* Contacts Layout */
	private Layout layoutContacts;
	private ItemList listContacts;
	private hu.gpsoft.device.api.app.component.Button btnAddContact;
	private hu.gpsoft.device.api.app.component.Button btnDeleteContact;
	private hu.gpsoft.device.api.app.component.Button btnCancelContact;
	
	/* Add Contact Layout */
	private Layout layoutAddContact;
	private hu.gpsoft.device.api.app.component.Label labelContactNickname;
	private hu.gpsoft.device.api.app.component.TextField fieldContactNickname;
	private hu.gpsoft.device.api.app.component.Label labelContactEmail;
	private hu.gpsoft.device.api.app.component.TextField fieldContactEmail;
	private hu.gpsoft.device.api.app.component.Button btnSaveContact;
	private hu.gpsoft.device.api.app.component.Button btnCancelAddContact;
	
	/* Insert Contact Layout */
	private Layout layoutInsertContact;
	private ItemList listContacts2;
	private hu.gpsoft.device.api.app.component.Button btnInsertContact;
	private hu.gpsoft.device.api.app.component.Button btnCancelInsertContact;

	private String currentName;
	private File attachedFile;
	
	private List<Contact> contacts;

	@Override
	public void init(@Nullable NBTTagCompound intent)
	{
		/* Loading Layout */
		layoutInit = new Layout(40, 40);

		spinnerInit = new Spinner(14, 10);
		layoutInit.addComponent(spinnerInit);

		labelLoading = new hu.gpsoft.device.api.app.component.Label("Loading...", 2, 26);
		layoutInit.addComponent(labelLoading);

		
		/* Main Menu Layout */

		layoutMainMenu = new Layout(200, 113);

		hu.gpsoft.device.api.app.component.Image image = new hu.gpsoft.device.api.app.component.Image(0, 0, layoutMainMenu.width, layoutMainMenu.height, 0, 0, 640, 360, 640, 360, ENDER_MAIL_BACKGROUND);
		image.setAlpha(0.85F);
		layoutMainMenu.addComponent(image);

		logo = new hu.gpsoft.device.api.app.component.Image(86, 20, 28, 28, info.getIconU(), info.getIconV(), 14, 14, 224, 224, Laptop.ICON_TEXTURES);
		layoutMainMenu.addComponent(logo);

		labelLogo = new hu.gpsoft.device.api.app.component.Label("Ender Mail", 100, 46);
		labelLogo.setAlignment(hu.gpsoft.device.api.app.Component.ALIGN_CENTER);
		layoutMainMenu.addComponent(labelLogo);

		btnRegisterAccount = new hu.gpsoft.device.api.app.component.Button(70, 65, "Register");
		btnRegisterAccount.setSize(60, 16);
		btnRegisterAccount.setClickListener((mouseX, mouseY, mouseButton) -> setCurrentLayout(layoutRegisterAccount));
		layoutMainMenu.addComponent(btnRegisterAccount);

		this.setCurrentLayout(layoutMainMenu);

		
		/* Register Account Layout */
		
		layoutRegisterAccount = new Layout(200, 113);

		image = new hu.gpsoft.device.api.app.component.Image(0, 0, layoutRegisterAccount.width, layoutRegisterAccount.height, 0, 0, 640, 360, 640, 360, ENDER_MAIL_BACKGROUND);
		image.setAlpha(0.85F);
		layoutRegisterAccount.addComponent(image);

		labelEmail = new hu.gpsoft.device.api.app.component.Label(TextFormatting.BOLD + "Choose your email", layoutRegisterAccount.width / 2, 30);
		labelEmail.setAlignment(hu.gpsoft.device.api.app.Component.ALIGN_CENTER);
		layoutRegisterAccount.addComponent(labelEmail);

		fieldEmail = new hu.gpsoft.device.api.app.component.TextField(20, 50, 80);
		layoutRegisterAccount.addComponent(fieldEmail);

		labelDomain = new hu.gpsoft.device.api.app.component.Label("@endermail.com", 105, 54);
		layoutRegisterAccount.addComponent(labelDomain);

		btnRegister = new hu.gpsoft.device.api.app.component.Button(70, 80, "Register");
		btnRegister.setSize(60, 16);
		btnRegister.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			int length = fieldEmail.getText().length();
			if(length > 0 && length <= 10)
			{
				TaskRegisterEmailAccount taskRegisterAccount = new TaskRegisterEmailAccount(fieldEmail.getText());
				taskRegisterAccount.setCallback((nbt, success) ->
				{
					if(success)
					{
						currentName = fieldEmail.getText();
						setCurrentLayout(layoutInbox);
					}
					else
					{
						fieldEmail.setTextColor(Color.RED);
					}
				});
				TaskManager.sendTask(taskRegisterAccount);
			}
		});
		layoutRegisterAccount.addComponent(btnRegister);

		
		/* Inbox Layout */
		
		layoutInbox = new Layout(260, 146);
		layoutInbox.setInitListener(new InitListener()
		{
			@Override
			public void onInit()
			{
				TaskUpdateInbox taskUpdateInbox = new TaskUpdateInbox();
				taskUpdateInbox.setCallback((nbt, success) ->
				{
                    listEmails.removeAll();
                    for (Email email : EmailManager.INSTANCE.getInbox())
                    {
                        listEmails.addItem(email);
                    }
                });
				TaskManager.sendTask(taskUpdateInbox);
			}
		});
		layoutInbox.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
		{
			mc.getTextureManager().bindTexture(ENDER_MAIL_BACKGROUND);
			RenderUtil.drawRectWithTexture(x, y, 0, 0, width, height, 640, 360, 640, 360);

			Color temp = new Color(Laptop.getSystem().getSettings().getColorScheme().getBackgroundColor());
			Color color = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 150);
			Gui.drawRect(x, y, x + 125, y + height, color.getRGB());
			Gui.drawRect(x + 125, y, x + 126, y + height, color.darker().getRGB());

			Email e = listEmails.getSelectedItem();
			if(e != null)
			{
				Gui.drawRect(x + 130, y + 5, x + width - 5, y + 34, color.getRGB());
				Gui.drawRect(x + 130, y + 34, x + width - 5, y + 35, color.darker().getRGB());
				Gui.drawRect(x + 130, y + 35, x + width - 5, y + height - 5, new Color(1.0F, 1.0F, 1.0F, 0.25F).getRGB());
				RenderUtil.drawStringClipped(e.getSubject(), x + 135, y + 10, 120, Color.WHITE.getRGB(), true);
				RenderUtil.drawStringClipped(e.getAuthor() + "@endermail.com", x + 135, y + 22, 120, Color.LIGHT_GRAY.getRGB(), false);
				Laptop.fontRenderer.drawSplitString(e.getMessage(), x + 135, y + 40, 115, Color.WHITE.getRGB());
			}
        });

		listEmails = new ItemList<>(5, 25, 116, 4);
		listEmails.setListItemRenderer(new ListItemRenderer<Email>(28)
		{
			@Override
			public void render(Email e, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected)
			{
				Gui.drawRect(x, y, x + width, y + height, selected ? Color.DARK_GRAY.getRGB() : Color.GRAY.getRGB());

				if (!e.isRead())
				{
					GlStateManager.color(1.0F, 1.0F, 1.0F);
					RenderUtil.drawApplicationIcon(info, x + width - 16, y + 2);
				}

				if(e.getAttachment() != null)
				{
					GlStateManager.color(1.0F, 1.0F, 1.0F);
					int posX = x + (!e.isRead() ? -12 : 0) + width;
					mc.getTextureManager().bindTexture(ENDER_MAIL_ICONS);
					RenderUtil.drawRectWithTexture(posX, y + 16, 20, 10, 7, 10, 13, 20);
				}
				RenderUtil.drawStringClipped(e.getSubject(), x + 5, y + 5, width - 20, Color.WHITE.getRGB(), false);
				RenderUtil.drawStringClipped(e.getAuthor() + "@endermail.com", x + 5, y + 17, width - 20, Color.LIGHT_GRAY.getRGB(), false);
			}
		});
		layoutInbox.addComponent(listEmails);

		btnViewEmail = new hu.gpsoft.device.api.app.component.Button(5, 5, ENDER_MAIL_ICONS, 30, 0, 10, 10);
		btnViewEmail.setClickListener((mouseX, mouseY, mouseButton) ->
		{
            int index = listEmails.getSelectedIndex();
            if (index != -1)
            {
                TaskManager.sendTask(new TaskViewEmail(index));
                Email email = listEmails.getSelectedItem();
                email.setRead(true);
                textMessage.setText(email.getMessage());
                labelViewSubject.setText(email.getSubject());
                labelFrom.setText(email.getAuthor() + "@endermail.com");
                attachedFile = email.getAttachment();
                if(attachedFile != null)
                {
                    btnSaveAttachment.setVisible(true);
                    labelAttachmentName.setVisible(true);
                    labelAttachmentName.setText(attachedFile.getName());
                }
                setCurrentLayout(layoutViewEmail);
            }
        });
		btnViewEmail.setToolTip("View", "Opens the currently selected email");
		layoutInbox.addComponent(btnViewEmail);

		btnNewEmail = new hu.gpsoft.device.api.app.component.Button(25, 5, ENDER_MAIL_ICONS, 0, 0, 10, 10);
		btnNewEmail.setClickListener((mouseX, mouseY, mouseButton) -> setCurrentLayout(layoutNewEmail));
		btnNewEmail.setToolTip("New Email", "Send an email to a player");
		layoutInbox.addComponent(btnNewEmail);

		btnReplyEmail = new hu.gpsoft.device.api.app.component.Button(45, 5, ENDER_MAIL_ICONS, 60, 0, 10, 10);
		btnReplyEmail.setClickListener((mouseX, mouseY, mouseButton) ->
		{
            Email email = listEmails.getSelectedItem();
            if (email != null)
            {
                setCurrentLayout(layoutNewEmail);
                fieldRecipient.setText(email.getAuthor() + "@endermail.com");
                fieldSubject.setText("RE: " + email.getSubject());
            }
        });
		btnReplyEmail.setToolTip("Reply", "Reply to the currently selected email");
		layoutInbox.addComponent(btnReplyEmail);

		btnDeleteEmail = new hu.gpsoft.device.api.app.component.Button(65, 5, ENDER_MAIL_ICONS, 10, 0, 10, 10);
		btnDeleteEmail.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			final int index = listEmails.getSelectedIndex();
			if(index != -1)
			{
				TaskDeleteEmail taskDeleteEmail = new TaskDeleteEmail(index);
				taskDeleteEmail.setCallback((nbt, success) ->
				{
					listEmails.removeItem(index);
					EmailManager.INSTANCE.getInbox().remove(index);
				});
				TaskManager.sendTask(taskDeleteEmail);
			}
		});
		btnDeleteEmail.setToolTip("Trash Email", "Deletes the currently select email");
		layoutInbox.addComponent(btnDeleteEmail);

		btnRefresh = new hu.gpsoft.device.api.app.component.Button(85, 5, ENDER_MAIL_ICONS, 20, 0, 10, 10);
		btnRefresh.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			TaskUpdateInbox taskUpdateInbox = new TaskUpdateInbox();
			taskUpdateInbox.setCallback((nbt, success) ->
			{
				listEmails.removeAll();
				for(Email email : EmailManager.INSTANCE.getInbox())
				{
					listEmails.addItem(email);
				}
			});
			TaskManager.sendTask(taskUpdateInbox);
		});
		btnRefresh.setToolTip("Refresh Inbox", "Checks for any new emails");
		layoutInbox.addComponent(btnRefresh);

		hu.gpsoft.device.api.app.component.Button btnSettings = new hu.gpsoft.device.api.app.component.Button(105, 5, Icons.WRENCH);
		layoutInbox.addComponent(btnSettings);

		
		/* New Email Layout */
		
		layoutNewEmail = new Layout(231, 148);
		layoutNewEmail.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
		{
			if(attachedFile != null)
			{
				AppInfo info = ApplicationManager.getApplication(attachedFile.getOpeningApp());
				RenderUtil.drawApplicationIcon(info, x + 46, y + 130);
			}
        });

		fieldRecipient = new hu.gpsoft.device.api.app.component.TextField(26, 5, 200);
		fieldRecipient.setPlaceholder("To");
		layoutNewEmail.addComponent(fieldRecipient);

		fieldSubject = new hu.gpsoft.device.api.app.component.TextField(26, 23, 200);
		fieldSubject.setPlaceholder("Subject");
		layoutNewEmail.addComponent(fieldSubject);

		textAreaMessage = new hu.gpsoft.device.api.app.component.TextArea(26, 41, 200, 85);
		textAreaMessage.setPlaceholder("Message");
		layoutNewEmail.addComponent(textAreaMessage);

		btnSendEmail = new hu.gpsoft.device.api.app.component.Button(5, 5, ENDER_MAIL_ICONS, 50, 0, 10, 10);
		btnSendEmail.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			Matcher matcher = EMAIL.matcher(fieldRecipient.getText());
			if(!matcher.matches()) return;

			Email email = new Email(fieldSubject.getText(), textAreaMessage.getText(), attachedFile);
			TaskSendEmail taskSendEmail = new TaskSendEmail(email, matcher.group(1));
			taskSendEmail.setCallback((nbt, success) ->
			{
				if(success)
				{
					setCurrentLayout(layoutInbox);
					textAreaMessage.clear();
					fieldSubject.clear();
					fieldRecipient.clear();
					resetAttachedFile();
				}
			});
			TaskManager.sendTask(taskSendEmail);
		});
		btnSendEmail.setToolTip("Send", "Send email to recipient");
		layoutNewEmail.addComponent(btnSendEmail);

		btnCancelEmail = new hu.gpsoft.device.api.app.component.Button(5, 25, ENDER_MAIL_ICONS, 40, 0, 10, 10);
		btnCancelEmail.setClickListener((mouseX, mouseY, mouseButton) ->
		{
            setCurrentLayout(layoutInbox);
            textAreaMessage.clear();
            fieldSubject.clear();
            fieldRecipient.clear();
            resetAttachedFile();
        });
		btnCancelEmail.setToolTip("Cancel", "Go back to Inbox");
		layoutNewEmail.addComponent(btnCancelEmail);

		btnAttachedFile = new hu.gpsoft.device.api.app.component.Button(26, 129, ENDER_MAIL_ICONS, 70, 0, 10, 10);
		btnAttachedFile.setToolTip("Attach File", "Select a file from computer to attach to this email");
		btnAttachedFile.setClickListener((mouseX, mouseY, mouseButton) ->
		{
            if(mouseButton == 0)
			{
				hu.gpsoft.device.api.app.Dialog.OpenFile dialog = new hu.gpsoft.device.api.app.Dialog.OpenFile(this);
				dialog.setResponseHandler((success, file) ->
				{
					if(!file.isFolder())
					{
						attachedFile = file.copy();
						labelAttachedFile.setText(file.getName());
						labelAttachedFile.left += 16;
						labelAttachedFile.xPosition += 16;
						btnAttachedFile.setVisible(false);
						btnRemoveAttachedFile.setVisible(true);
						dialog.close();
					}
					else
					{
						openDialog(new hu.gpsoft.device.api.app.Dialog.Message("Attachment must be a file!"));
					}
					return false;
				});
				openDialog(dialog);
			}
        });
		layoutNewEmail.addComponent(btnAttachedFile);

		btnRemoveAttachedFile = new hu.gpsoft.device.api.app.component.Button(26, 129, ENDER_MAIL_ICONS, 40, 0, 10, 10);
		btnRemoveAttachedFile.setToolTip("Remove Attachment", "Delete the attached file from this email");
		btnRemoveAttachedFile.setVisible(false);
		btnRemoveAttachedFile.setClickListener((mouseX, mouseY, mouseButton) ->
		{
            if(mouseButton == 0)
			{
				resetAttachedFile();
			}
        });
		layoutNewEmail.addComponent(btnRemoveAttachedFile);

		labelAttachedFile = new hu.gpsoft.device.api.app.component.Label("No file attached", 46, 133);
		layoutNewEmail.addComponent(labelAttachedFile);
		
		
		/* View Email Layout */
		
		layoutViewEmail = new Layout(240, 156);
		layoutViewEmail.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
		{
            Gui.drawRect(x, y + 22, x + layoutViewEmail.width, y + 50, Color.GRAY.getRGB());
            Gui.drawRect(x, y + 22, x + layoutViewEmail.width, y + 23, Color.DARK_GRAY.getRGB());
            Gui.drawRect(x, y + 49, x + layoutViewEmail.width, y + 50, Color.DARK_GRAY.getRGB());
            Gui.drawRect(x, y + 50, x + layoutViewEmail.width, y + 156, COLOR_EMAIL_CONTENT_BACKGROUND.getRGB());

			if(attachedFile != null)
			{
				GlStateManager.color(1.0F, 1.0F, 1.0F);
				AppInfo info = ApplicationManager.getApplication(attachedFile.getOpeningApp());
				RenderUtil.drawApplicationIcon(info, x + 204, y + 4);
			}
		});

		labelViewSubject = new hu.gpsoft.device.api.app.component.Label("Subject", 5, 26);
		labelViewSubject.setTextColor(new Color(255, 170, 0));
		layoutViewEmail.addComponent(labelViewSubject);

		labelFrom = new hu.gpsoft.device.api.app.component.Label("From", 5, 38);
		layoutViewEmail.addComponent(labelFrom);

		btnCancelViewEmail = new hu.gpsoft.device.api.app.component.Button(5, 3, ENDER_MAIL_ICONS, 40, 0, 10, 10);
		btnCancelViewEmail.setClickListener((mouseX, mouseY, mouseButton) ->
		{
			if(mouseButton == 0)
			{
				attachedFile = null;
				btnSaveAttachment.setVisible(false);
				labelAttachmentName.setVisible(false);
				setCurrentLayout(layoutInbox);
			}

		});
		btnCancelViewEmail.setToolTip("Cancel", "Go back to Inbox");
		layoutViewEmail.addComponent(btnCancelViewEmail);

		textMessage = new Text("Hallo", 5, 54, 230);
		textMessage.setShadow(false);
		layoutViewEmail.addComponent(textMessage);

		btnSaveAttachment = new hu.gpsoft.device.api.app.component.Button(219, 3, ENDER_MAIL_ICONS, 80, 0, 10, 10);
		btnSaveAttachment.setToolTip("Save Attachment", "Save the file attached to this email");
		btnSaveAttachment.setVisible(false);
		btnSaveAttachment.setClickListener((mouseX, mouseY, mouseButton) ->
		{
            if(mouseButton == 0 && attachedFile != null)
            {
            	hu.gpsoft.device.api.app.Dialog.SaveFile dialog = new hu.gpsoft.device.api.app.Dialog.SaveFile(this, attachedFile);
            	openDialog(dialog);
			}
        });
		layoutViewEmail.addComponent(btnSaveAttachment);

		labelAttachmentName = new Label("", 200, 7);
		labelAttachmentName.setVisible(false);
		labelAttachmentName.setAlignment(Component.ALIGN_RIGHT);
		layoutViewEmail.addComponent(labelAttachmentName);

		this.setCurrentLayout(layoutInit);

		TaskCheckEmailAccount taskCheckAccount = new TaskCheckEmailAccount();
		taskCheckAccount.setCallback((nbt, success) ->
		{
            if (success)
            {
                currentName = nbt.getString("Name");
                listEmails.removeAll();
                for (Email email : EmailManager.INSTANCE.getInbox())
                {
                    listEmails.addItem(email);
                }
                setCurrentLayout(layoutInbox);
            }
            else
            {
                btnRegisterAccount.setVisible(true);
                setCurrentLayout(layoutMainMenu);
            }
        });
		TaskManager.sendTask(taskCheckAccount);
	}

	private void resetAttachedFile()
	{
		if(attachedFile != null)
		{
			labelAttachedFile.setText("No file attached");
			labelAttachedFile.left -= 16;
			labelAttachedFile.xPosition -= 16;
			btnRemoveAttachedFile.setVisible(false);
			btnAttachedFile.setVisible(true);
			attachedFile = null;
		}
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
	public String getWindowTitle()
	{
		if (getCurrentLayout() == layoutInbox)
		{
			return "Inbox: " + currentName + "@endermail.com";
		}
		if(getCurrentLayout() == layoutContacts)
		{
			return "Contacts";
		}
		return info.getName();
	}

	@Override
	public void onClose()
	{
		super.onClose();
		attachedFile = null;
	}
}
