//
// Created by mathd on 2026-11-17.
//

#include <iostream>
#include <gtk/gtk.h>

static void activate(GtkApplication *app, gpointer user_data) {
    GtkWidget *window;
    GtkWidget *button;

    window = gtk_application_window_new(app);
    gtk_window_set_title(GTK_WINDOW(window), "Install bedrockR");
    gtk_window_set_default_size(GTK_WINDOW(window), 800, 400);
    gtk_window_set_resizable(GTK_WINDOW(window), FALSE);

    button = gtk_button_new_with_label("fsffv");
    gtk_widget_set_halign(button, GTK_ALIGN_CENTER);
    gtk_widget_set_valign(button, GTK_ALIGN_END);
    gtk_window_set_child(GTK_WINDOW(window), button);

    gtk_window_present(GTK_WINDOW(window));
}

int main(int argc, char **argv) {
#ifdef _WIN32
    _putenv_s("GTK_CSD", "0");
    _putenv_s("GTK_THEME", "bedrockr-light");
#else
    setenv("GTK_CSD", "0", 1);
    setenv("GTK_THEME", "bedrockr-light");
#endif

    GtkApplication *app;
    int status;
    app = gtk_application_new("fn10.bedrockr.installer", G_APPLICATION_DEFAULT_FLAGS);
    g_signal_connect(app, "activate", G_CALLBACK (activate), NULL);
    status = g_application_run(G_APPLICATION(app), argc, argv);
    g_object_unref(app);

    return status;
}
