package org.codetome.zircon.internal.component.impl

import org.codetome.zircon.api.Modifiers
import org.codetome.zircon.api.Position
import org.codetome.zircon.api.Size
import org.codetome.zircon.api.builder.ComponentStylesBuilder
import org.codetome.zircon.api.builder.StyleSetBuilder
import org.codetome.zircon.api.component.Button
import org.codetome.zircon.api.component.ComponentStyles
import org.codetome.zircon.api.component.ColorTheme
import org.codetome.zircon.api.factory.TextColorFactory
import org.codetome.zircon.api.input.Input
import org.codetome.zircon.api.input.MouseAction
import org.codetome.zircon.internal.component.WrappingStrategy
import org.codetome.zircon.internal.event.EventBus
import org.codetome.zircon.internal.event.EventType
import java.util.*

class DefaultButton(private val text: String,
                    private val wrappers: Deque<WrappingStrategy>,
                    initialSize: Size,
                    position: Position,
                    componentStyles: ComponentStyles)
    : Button, DefaultComponent(initialSize = initialSize,
        position = position,
        componentStyles = componentStyles,
        wrappers = wrappers) {

    init {
        getDrawSurface().putText(text, getWrapperOffset())

        EventBus.subscribe<MouseAction>(EventType.MousePressed(getId()), {
            getDrawSurface().applyStyle(getComponentStyles().activate())
            EventBus.emit(EventType.ComponentChange)
        })
        EventBus.subscribe<MouseAction>(EventType.MouseReleased(getId()), {
            getDrawSurface().applyStyle(getComponentStyles().mouseOver())
            EventBus.emit(EventType.ComponentChange)
        })
    }

    override fun acceptsFocus(): Boolean {
        return true
    }

    override fun giveFocus(input: Optional<Input>): Boolean {
        getDrawSurface().applyStyle(getComponentStyles().giveFocus())
        EventBus.emit(EventType.ComponentChange)
        return true
    }

    override fun takeFocus(input: Optional<Input>) {
        getDrawSurface().applyStyle(getComponentStyles().reset())
        EventBus.emit(EventType.ComponentChange)
//        var currSize = getEffectiveSize()
//        var currentOffset = Position.DEFAULT_POSITION
//        wrappers.forEach {
//            currSize += it.getOccupiedSize()
//            if(it === BOX_HIGHLIGHT) {
//                it.remove(getDrawSurface(), currSize, currentOffset, getComponentStyles().getCurrentStyle())
//            }
//            currentOffset += it.getWrapperOffset()
//        }
//        wrappers.remove(BOX_HIGHLIGHT)
    }

    override fun getText() = text

    override fun applyTheme(colorTheme: ColorTheme) {
        setComponentStyles(ComponentStylesBuilder.newBuilder()
                .defaultStyle(StyleSetBuilder.newBuilder()
                        .foregroundColor(colorTheme.getAccentColor())
                        .backgroundColor(TextColorFactory.TRANSPARENT)
                        .build())
                .mouseOverStyle(StyleSetBuilder.newBuilder()
                        .foregroundColor(colorTheme.getDarkBackgroundColor())
                        .backgroundColor(colorTheme.getAccentColor())
                        .build())
                .focusedStyle(StyleSetBuilder.newBuilder()
                        .foregroundColor(colorTheme.getDarkBackgroundColor())
                        .backgroundColor(colorTheme.getAccentColor())
                        .build())
                .activeStyle(StyleSetBuilder.newBuilder()
                        .foregroundColor(colorTheme.getDarkForegroundColor())
                        .backgroundColor(colorTheme.getAccentColor())
                        .build())
                .build())
    }

    companion object {
        // TODO: fix this later
        val BOX_HIGHLIGHT = BorderWrappingStrategy(Modifiers.BORDER.of(Modifiers.BorderType.DOTTED))
    }
}