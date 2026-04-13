package com.plovdev.pornviewer.models;

import com.plovdev.pornviewer.events.listeners.ClickListener;
import com.plovdev.pornviewer.httpquering.defimpl.PBPornHandler;
import com.plovdev.pornviewer.utility.Globals;
import com.plovdev.pornviewer.utility.sharing.ShareParameter;
import com.plovdev.pornviewer.utility.sharing.Sharer;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;

public class ModelCard extends PornCard {
    private static final Logger log = LoggerFactory.getLogger(ModelCard.class);
    private ModelInfo modelInfo;
    private final Pane pane;

    public void setModelInfo(ModelInfo modelInfo) {
        this.modelInfo = modelInfo;
    }

    public void setModelInfo() {
    }

    public ModelInfo getModelInfo() {
        return modelInfo;
    }

    public Pane getPane() {
        return pane;
    }

    public ModelCard(ModelInfo modelInfo, Pane p) {
        this.modelInfo = modelInfo;
        pane = p;
    }

    @Override
    public void render() {
        try {
            StackPane mainContainer = new StackPane();
            PBPornHandler handler = new PBPornHandler();

            Image image;
            String pic = modelInfo.getAvatar();
            if (pic.endsWith(".webp")) {
                image = SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(handler.getBytes(pic))), null);
            } else {
                image = new Image(pic);
            }
            ImageView view = new ImageView(image);

            view.setSmooth(true);
            view.getStyleClass().add("video-preview");
            view.setPreserveRatio(true);

            Hyperlink titleLabel = new Hyperlink(modelInfo.getRusName());
            setTitle(modelInfo.getRusName());
            titleLabel.setOnAction(e -> ClickListener.notifyListeners(modelInfo));
            titleLabel.getStyleClass().add("video-title");

            HBox hBox = new HBox(titleLabel);
            hBox.getStyleClass().add("title-box");
            VBox box = new VBox(10, view, hBox);
            box.getStyleClass().add("trailer");

            view.fitWidthProperty().bind(pane.widthProperty().divide(3.5));
            view.fitWidthProperty().bind(pane.widthProperty().divide(3.5));
            view.fitWidthProperty().bind(pane.widthProperty().divide(3.5));

            box.minWidthProperty().bind(pane.widthProperty().divide(3.5));
            box.prefWidthProperty().bind(pane.widthProperty().divide(3.5));
            box.maxWidthProperty().bind(pane.widthProperty().divide(3.5));

            Label county = new Label(modelInfo.getCountry());
            county.getStyleClass().add("model-country");

            Label videos = new Label(String.valueOf(modelInfo.getVideos()));
            videos.getStyleClass().add("marker-download");

            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);

            HBox infoOverlay = new HBox(county, region, new VBox(10, videos, getShareButton(Globals.getPrimaryStage(), this)));
            infoOverlay.setVisible(false);
            infoOverlay.getStyleClass().add("model-hover");

            // Позиционируем overlay поверх изображения
            StackPane.setAlignment(infoOverlay, Pos.BOTTOM_CENTER);
            StackPane.setMargin(infoOverlay, new Insets(0, 0, 50, 0));

            // Обработчики наведения
            mainContainer.setOnMouseEntered(e -> infoOverlay.setVisible(true));
            mainContainer.setOnMouseExited(e -> infoOverlay.setVisible(false));

            mainContainer.getChildren().addAll(box, infoOverlay);
            getChildren().add(mainContainer);
        } catch (Exception e) {
            log.error("Model rendering error: ", e);
        }
    }

    private Button getShareButton(Stage stage, ModelCard card) {
        Button shareButton = new Button();
        shareButton.getStyleClass().add("share-button");
        SVGPath shareIcon = new SVGPath();
        shareIcon.setContent("M1613,203a2.967,2.967,0,0,1-1.86-.661l-3.22,2.01a2.689,2.689,0,0,1,0,1.3l3.22,2.01A2.961,2.961,0,0,1,1613,207a3,3,0,1,1-3,3,3.47,3.47,0,0,1,.07-0.651l-3.21-2.01a3,3,0,1,1,0-4.678l3.21-2.01A3.472,3.472,0,0,1,1610,200,3,3,0,1,1,1613,203Zm0,8a1,1,0,1,0-1-1A1,1,0,0,0,1613,211Zm-8-7a1,1,0,1,0,1,1A1,1,0,0,0,1605,204Zm8-5a1,1,0,1,0,1,1A1,1,0,0,0,1613,199Z");
        shareIcon.setStroke(Color.WHITE);
        shareIcon.setScaleY(1.5);
        shareIcon.setScaleX(1.5);
        shareIcon.setStrokeWidth(1);
        shareIcon.setFill(Color.TRANSPARENT);

        shareButton.setGraphic(shareIcon);
        String modelName = modelInfo.getUrl().substring(modelInfo.getUrl().lastIndexOf("/") + 1);
        shareButton.setOnAction(e -> Sharer.share(stage, card, new ShareParameter("name", modelName)));

        return shareButton;
    }
}