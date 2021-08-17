package io.github.ashisbored.particular.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.ashisbored.particular.renderer.ParticleScene;
import io.github.ashisbored.particular.renderer.SceneManager;
import io.github.ashisbored.particular.renderer.objects.LineParticleObject;
import io.github.ashisbored.particular.renderer.objects.SphereParticleObject;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.DoubleArgumentType.getDouble;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.command.argument.IdentifierArgumentType.getIdentifier;
import static net.minecraft.command.argument.IdentifierArgumentType.identifier;
import static net.minecraft.command.argument.Vec3ArgumentType.getVec3;
import static net.minecraft.command.argument.Vec3ArgumentType.vec3;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ParticularCommand {
    private static final SimpleCommandExceptionType ID_IN_USE = new SimpleCommandExceptionType(new LiteralMessage("That ID is already in use!"));
    private static final SimpleCommandExceptionType UNKNOWN_SCENE = new SimpleCommandExceptionType(new LiteralMessage("No scene with that ID!"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("particular")
                        .requires(ctx -> ctx.hasPermissionLevel(2))
                        .then(literal("create")
                                .then(createSceneCommand())
                                .then(createObjectCommand())
                        ).then(literal("remove").then(argument("scene", identifier()).executes(ParticularCommand::removeScene)))
        );
    }

    private static LiteralArgumentBuilder<ServerCommandSource> createSceneCommand() {
        return literal("scene").then(
                argument("id", identifier())
                        .then(argument("renderInterval", integer(0))
                                .executes(ctx -> createScene(ctx, getInteger(ctx, "renderInterval"))))
                        .executes(ctx -> createScene(ctx, 5)));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> createObjectCommand() {
        return literal("object")
                .then(argument("scene", identifier())
                        .then(literal("sphere")
                                .then(argument("pos", vec3())
                                        .then(argument("radius", doubleArg(0))
                                                .then(argument("density", integer(0))
                                                        .then(argument("colour", integer(0, 0xffffff)) // TODO: Make this support hex values
                                                                .executes(ParticularCommand::createSphere))
                                                )
                                        )
                                )
                        ).then(literal("line")
                                .then(argument("start", vec3())
                                        .then(argument("end", vec3())
                                                .then(argument("steps", integer(1))
                                                        .then(argument("colour", integer(0, 0xffffff))
                                                                .executes(ParticularCommand::createLine)
                                                        )
                                                )
                                        )
                                )
                        )
                );
    }

    private static int removeScene(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Identifier id = getIdentifier(ctx, "scene");
        if (!SceneManager.removeScene(id)) {
            throw UNKNOWN_SCENE.create();
        }
        ctx.getSource().sendFeedback(new LiteralText("Removed scene with id: " + id), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int createLine(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Identifier sceneId = getIdentifier(ctx, "scene");
        Vec3d start = getVec3(ctx, "start");
        Vec3d end = getVec3(ctx, "end");
        int steps = getInteger(ctx, "steps");
        int colour = getInteger(ctx, "colour");

        ParticleScene scene = SceneManager.getById(sceneId);
        if (scene == null) {
            throw UNKNOWN_SCENE.create();
        }

        LineParticleObject line = new LineParticleObject(
                scene,
                () -> new DustParticleEffect(new Vec3f(Vec3d.unpackRgb(colour)), 1),
                start, end, steps);

        scene.addObject(line);
        ctx.getSource().sendFeedback(new LiteralText("Added line to scene with id: " + sceneId), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int createSphere(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Identifier sceneId = getIdentifier(ctx, "scene");
        Vec3d pos = getVec3(ctx, "pos");
        double radius = getDouble(ctx, "radius");
        int density = getInteger(ctx, "density");
        int colour = getInteger(ctx, "colour");

        ParticleScene scene = SceneManager.getById(sceneId);
        if (scene == null) {
            throw UNKNOWN_SCENE.create();
        }

        SphereParticleObject sphere = new SphereParticleObject(
                scene,
                () -> new DustParticleEffect(new Vec3f(Vec3d.unpackRgb(colour)), 1),
                pos,
                radius,
                density);
        scene.addObject(sphere);
        ctx.getSource().sendFeedback(new LiteralText("Added sphere to scene with id: " + sceneId), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int createScene(CommandContext<ServerCommandSource> ctx, int renderInterval) throws CommandSyntaxException {
        Identifier id = getIdentifier(ctx, "id");
        ServerWorld world = ctx.getSource().getWorld();
        if (!SceneManager.isUnusedId(id)) {
            throw ID_IN_USE.create();
        }
        SceneManager.create(world, id, renderInterval);
        ctx.getSource().sendFeedback(new LiteralText("Created scene with id: " + id), true);

        return Command.SINGLE_SUCCESS;
    }
}
