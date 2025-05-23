package edu.upc.prop.clusterxx.presentacio.vistes;

import edu.upc.prop.clusterxx.Fitxa;
import edu.upc.prop.clusterxx.presentacio.ColorLoader;
import edu.upc.prop.clusterxx.presentacio.FontLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class FitxaVista extends JPanel {
    private final Fitxa fitxa;
    private boolean seleccionada;
    private final Color colorFons = ColorLoader.getInstance().getColorFonsFitxa(); // Color de fons per defecte
    private final Color colorSeleccionada = ColorLoader.getInstance().getColorSeleccionada();
    private final Color colorText = Color.BLACK;
    private Font fontLletra = FontLoader.getCustomFont(20f);
    private final Font fontPunts = FontLoader.getCustomFont(20f);

    public FitxaVista(Fitxa fitxa) {
        this.fitxa = fitxa;
        this.seleccionada = false;
        setPreferredSize(new Dimension(50, 50));
        setMinimumSize(new Dimension(50, 50));
        setBackground(colorFons);
        setBorder(BorderFactory.createLineBorder(new Color(120, 100, 60), 2)); // Borde més definit
        setOpaque(true); // Assegura que el fons sigui visible

        // Afegir esdeveniment de selecció
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setSeleccionada(!seleccionada); // Inverteix l'estat de selecció quan es fa clic
            }
        });
    }

    public FitxaVista(Fitxa fitxa, int ample, int alt, int midaFont) {
        this.fitxa = fitxa;
        this.seleccionada = false;
        setPreferredSize(new Dimension(ample, alt));
        setMinimumSize(new Dimension(ample, alt));
        setBackground(colorFons);
        setBorder(BorderFactory.createLineBorder(new Color(120, 100, 60), 3));
        setOpaque(true);
        fontLletra = FontLoader.getCustomFont(midaFont);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setSeleccionada(!seleccionada); // Inverteix l'estat de selecció quan es fa clic
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Habilitar antialiasing per millorar la qualitat del dibuix
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibuixar l'efecte de selecció
        if (seleccionada) {
            g2d.setColor(new Color(255, 255, 200, 120)); // Sombra suau
            g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 10, 10); // Contorn arrodonit
        }

        // Dibuixar la lletra
        g2d.setColor(colorText);
        g2d.setFont(fontLletra);

        String lletra = fitxa.obtenirLletra();
        FontMetrics fmLletra = g2d.getFontMetrics();
        int ampleLletra = fmLletra.stringWidth(lletra);

        g2d.drawString(lletra, (getWidth() - ampleLletra) / 2, getHeight() / 2 + fmLletra.getAscent() / 2 - 4);

        // Dibuixar els punts
        g2d.setFont(fontPunts);
        String punts = String.valueOf(fitxa.obtenirPunts());
        if (fitxa.obtenirPunts() < 10) punts = " " + punts; // petit padding
        g2d.drawString(punts, getWidth() - 20, getHeight() - 6);
    }

    /**
     * Mètode per actualitzar la selecció visual de la fitxa.
     * @param seleccionada indica si la fitxa està seleccionada o no
     */
    public void setSeleccionada(boolean seleccionada) {
        this.seleccionada = seleccionada;
        setBackground(seleccionada ? colorSeleccionada : colorFons); // Canviar el fons
        repaint(); // Redibuixar el panell per actualitzar la vista
    }

    /**
     * Retorna la fitxa associada a aquesta vista.
     */
    public Fitxa obtenirFitxa() {
        return fitxa;
    }
}
