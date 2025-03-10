import java.awt.*
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import java.io.IOException
import java.net.URI
import javax.swing.*


class Tab : JPanel() {
    private val c = GridBagConstraints()
    private val userNameField = JTextField(settingHolder.setting.loginId, 64)
    private val linkLabel = JLabel("Click here to generate gmail app password").apply {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
    }

    private val userPasswordField = JPasswordField(settingHolder.setting.password, 64)
    private val fromMailField = JTextField(settingHolder.setting.from, 64)
    private val toMailField = JTextField(settingHolder.setting.to, 64)
    private val enableField = JCheckBox("Enabled", settingHolder.setting.enable)
    private val testSend = JButton("Send Test Mail")
    private val settingButton = JButton(ImageIcon(javaClass.getResource("/setting_icon.png"))).apply {
        isContentAreaFilled = false
        isBorderPainted = false
        isFocusPainted = false
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                settingPopupMenu().show(e.component, e.x, e.y)
            }
        })
    }

    private var isUpdating = false

    private fun enableUpdateInputs(boolean: Boolean) {
        userNameField.isEditable = boolean
        userPasswordField.isEditable = boolean
        fromMailField.isEditable = boolean
        toMailField.isEditable = boolean
        testSend.isEnabled = boolean
    }

    private fun updateFields(newSetting: Setting) {
        if (isUpdating) return // Prevent infinite loop during updates

        isUpdating = true
        SwingUtilities.invokeLater {
//            Api.log("Update Setting ${settingHolder.setting}")
            userNameField.text = newSetting.loginId
            userPasswordField.text = newSetting.password
            fromMailField.text = newSetting.from
            toMailField.text = newSetting.to
            enableField.isSelected = newSetting.enable
            enableUpdateInputs(newSetting.enable)
            isUpdating = false
        }
    }

    init {
        layout = GridBagLayout()
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10) // Add padding around the panel

        c.fill = GridBagConstraints.HORIZONTAL // Fill horizontally
        c.insets = Insets(5, 5, 5, 5) // Add some padding around components

        add(enableField, c)

        // Username field
        addLabeledComponent("Login ID:", userNameField, 1)

        // Password field
        addLabeledComponent("Gmail app password:", JSplitPane(JSplitPane.HORIZONTAL_SPLIT,userPasswordField,linkLabel).apply { setResizeWeight(0.5) },2)

        // From email field
        addLabeledComponent("From Email:", fromMailField, 3)

        // To email field
        addLabeledComponent("To Email:", toMailField, 4)

        c.gridx = 0
        c.gridy = 5
        c.gridwidth = 2

        add(testSend, c)
        c.gridy = 6
        add(settingButton,c)

        // Add focus listeners to update the `settingHolder` when focus is lost
        addFocusListenerToTextField(userNameField) {
            settingHolder.setting = settingHolder.setting.copy(loginId = userNameField.text)
        }
        addFocusListenerToTextField(userPasswordField) {
            settingHolder.setting = settingHolder.setting.copy(password = String(userPasswordField.password))
        }
        addFocusListenerToTextField(fromMailField) {
            settingHolder.setting = settingHolder.setting.copy(from = fromMailField.text)
        }
        addFocusListenerToTextField(toMailField) {
            settingHolder.setting = settingHolder.setting.copy(to = toMailField.text)
        }

        enableField.addActionListener {
            settingHolder.setting = settingHolder.setting.copy(enable = enableField.isSelected)
        }

        linkLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                try {
                    // OpenAIのURLを開く
                    Desktop.getDesktop().browse(URI("https://myaccount.google.com/apppasswords"))
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        })

        testSend.addActionListener {
            object : SwingWorker<Void, Void>() {
                override fun doInBackground(): Void? {
                    Sender().send(true)  // Run in background thread
                    return null
                }

                override fun done() {
                    // Once the background task is done, log on the UI thread
                    Api.log("Sent test mail: ${settingHolder.setting.to} -> ${settingHolder.setting.from}")
                }
            }.execute()
        }

        // Add observer to update fields when `settingHolder.setting` changes
        settingHolder.addObserver { newSetting ->
            if (!isUpdating) { // Avoid infinite loop when updating settings
                updateFields(newSetting)
            }
        }

        // Initial update of fields
        updateFields(settingHolder.setting)
    }

    private fun addLabeledComponent(labelText: String, component: JComponent, gridy: Int) {
        val label = JLabel(labelText)

        // Label
        c.gridx = 0
        c.gridy = gridy
        c.gridwidth = 1
        add(label, c)

        // Component
        c.gridx = 1
        c.gridy = gridy
        c.gridwidth = 2
        add(component, c)
    }

    private fun addFocusListenerToTextField(textField: JTextField, action: () -> Unit) {
        textField.addFocusListener(object : FocusAdapter() {
            override fun focusLost(e: FocusEvent?) {
                action()
            }
        })
    }

    private fun addFocusListenerToTextField(passwordField: JPasswordField, action: () -> Unit) {
        passwordField.addFocusListener(object : FocusAdapter() {
            override fun focusLost(e: FocusEvent?) {
                action()
            }
        })
    }

    private fun showFileChooserDialog(action: String, onFileSelected: (File) -> Unit) {
        val fileChooser = JFileChooser()
        val result = if (action == "save") fileChooser.showSaveDialog(null) else fileChooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFile = fileChooser.selectedFile

            // Check file extension
            if (selectedFile.extension.lowercase() != "json") {
                JOptionPane.showMessageDialog(
                    null,
                    "File extension must be '.json'",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
                return
            }
            onFileSelected(selectedFile)
        }
    }
    private fun alert(message: String) {
        JOptionPane.showMessageDialog(this, message, "burpee", JOptionPane.INFORMATION_MESSAGE)
    }
    private fun settingPopupMenu(): JPopupMenu {
        return JPopupMenu().apply {

            val saveItem = JMenuItem("Save Setting").apply {
                addActionListener {
                    showFileChooserDialog("save") { selectedFile ->
                        try {
                            IOJson().exportJson(selectedFile)
                        } catch (e: IOException) {
                            alert("Failed to write to ${selectedFile.name}: ${e.message}")
                        }
                    }
                }
            }
            val loadMenuItem = JMenuItem("Load Setting")
                .apply {
                    addActionListener {
                        showFileChooserDialog("open") { selectedFile ->
                            try {
                                IOJson().loadJson(selectedFile)
                            } catch (e: IOException) {
                                alert("Failed to load ${selectedFile.name}: ${e.message}")
                            }
                        }
                    }
                }

            val resetMenuItem = JMenuItem("Reset Setting")
                .apply {
                    addActionListener {
                        showConfirmationDialog(
                            "Reset setting?",
                            "Confirmation"
                        ) { isOk ->
                            if (isOk) {
                                settingHolder.setting = Setting()
                            }
                        }
                    }
                }

            add(saveItem)
            add(loadMenuItem)
            add(resetMenuItem)
        }
    }
    private fun showConfirmationDialog(message: String, title: String, callback: (Boolean) -> Unit) {
        val result = JOptionPane.showConfirmDialog(
            null,
            message,
            title,
            JOptionPane.OK_CANCEL_OPTION
        )
        callback(result == JOptionPane.OK_OPTION)
    }
}
