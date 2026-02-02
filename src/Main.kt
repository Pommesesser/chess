import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.SwingUtilities

const val DIM = 400

fun main() {
    SwingUtilities.invokeLater {
        val frame = JFrame()
        frame.isResizable = false
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        val gamePanel = GamePanel()
        gamePanel.preferredSize = Dimension(DIM, DIM)
        frame.add(gamePanel)

        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }
}