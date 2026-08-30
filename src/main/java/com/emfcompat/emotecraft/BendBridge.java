package com.emfcompat.emotecraft;

import dev.kosmx.playerAnim.core.util.Pair;
import io.github.kosmx.bendylib.ModelPartAccessor;
import io.github.kosmx.bendylib.MutableCuboid;
import io.github.kosmx.bendylib.impl.BendableCuboid;
import io.github.kosmx.bendylib.impl.ICuboid;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import traben.entity_model_features.models.parts.EMFModelPartWithState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Connects Player Animator's logical limb bend to every physical cube held by
 * EMF, including cubes stored in inactive CEM variants.
 */
public final class BendBridge {
    private static final String BEND_MUTATOR = "bend";
    private static final float MINIMUM_BEND = 1.0E-4F;
    private static final Map<ModelPart, BendSpec> SPECS =
            Collections.synchronizedMap(new WeakHashMap<>());

    private BendBridge() {
    }

    public static boolean isEmfPart(ModelPart part) {
        return part instanceof EMFModelPartWithState;
    }

    public static void initialize(ModelPart part, Direction direction) {
        BendSpec spec = new BendSpec(direction, false);
        SPECS.put(part, spec);
        registerAll(part, spec);
    }

    public static void initializeCape(ModelPart part) {
        BendSpec spec = new BendSpec(Direction.UP, true);
        SPECS.put(part, spec);
        registerAll(part, spec);
    }

    public static void apply(ModelPart part, Pair<Float, Float> bend) {
        if (bend == null) {
            reset(part);
            return;
        }
        apply(part, bend.getLeft(), bend.getRight());
    }

    public static void apply(ModelPart part, float axis, float amount) {
        List<MutableCuboid> cubes = collectCuboids(part);
        // Если кубов больше 4, игнорируем сгиб для этой конечности
        if (cubes.size() > 3) {
            return;
        }

        BendSpec spec = SPECS.computeIfAbsent(part, ignored -> new BendSpec(Direction.UP, false));
        for (MutableCuboid cube : cubes) {
            ensureRegistered(cube, spec);
            if (Math.abs(amount) < MINIMUM_BEND) {
                cube.getAndActivateMutator(null);
                continue;
            }

            ICuboid mutator = cube.getAndActivateMutator(BEND_MUTATOR);
            if (mutator instanceof BendableCuboid bendable) {
                bendable.applyBend(axis, amount);
            }
        }
    }

    public static void reset(ModelPart part) {
        for (MutableCuboid cube : collectCuboids(part)) {
            cube.getAndActivateMutator(null);
        }
    }

//    public static void copy(ModelPart source, ModelPart target) {
//        if (!isEmfPart(source) || !isEmfPart(target)) {
//            return;
//        }
//
//        BendSpec targetSpec = SPECS.get(target);
//        if (targetSpec == null) {
//            targetSpec = SPECS.getOrDefault(source, new BendSpec(Direction.UP, false));
//            SPECS.put(target, targetSpec);
//        }
//
//        List<MutableCuboid> sourceCubes = collectCuboids(source);
//        List<MutableCuboid> targetCubes = collectCuboids(target);
//
//        // Копируем состояние для каждого соответствующего куба (будь то суставы конечностей или сабмодели тела)
//        int commonSize = Math.min(sourceCubes.size(), targetCubes.size());
//        for (int index = 0; index < commonSize; index++) {
//            MutableCuboid destination = targetCubes.get(index);
//            ensureRegistered(destination, targetSpec);
//            destination.copyStateFrom(sourceCubes.get(index));
//        }
//    }

    public static void copy(ModelPart source, ModelPart target) {
        if (!isEmfPart(source) || !isEmfPart(target)) {
            return;
        }

        List<MutableCuboid> sourceCubes = collectCuboids(source);
        List<MutableCuboid> targetCubes = collectCuboids(target);

        // Пропускаем копирование, если модель сложная
        if (sourceCubes.size() > 3 || targetCubes.size() > 3) {
            return;
        }

        BendSpec targetSpec = SPECS.get(target);
        if (targetSpec == null) {
            targetSpec = SPECS.getOrDefault(source, new BendSpec(Direction.UP, false));
            SPECS.put(target, targetSpec);
        }

        int commonSize = Math.min(sourceCubes.size(), targetCubes.size());
        for (int index = 0; index < commonSize; index++) {
            MutableCuboid destination = targetCubes.get(index);
            ensureRegistered(destination, targetSpec);
            destination.copyStateFrom(sourceCubes.get(index));
        }
    }

//    private static void registerAll(ModelPart part, BendSpec spec) {
//        for (MutableCuboid cube : collectCuboids(part)) {
//            ensureRegistered(cube, spec);
//        }
//    }

    private static void registerAll(ModelPart part, BendSpec spec) {
        List<MutableCuboid> cubes = collectCuboids(part);
        // Если кубов слишком много (сложная составная модель с сабмоделями), не регистрируем сгиб
        if (cubes.size() > 3) {
            return;
        }
        for (MutableCuboid cube : cubes) {
            ensureRegistered(cube, spec);
        }
    }

    private static void ensureRegistered(MutableCuboid cube, BendSpec spec) {
        if (cube.hasMutator(BEND_MUTATOR)) {
            return;
        }

        cube.registerMutator(BEND_MUTATOR, data -> {
            // В версии 1.20.1 ручное смещение pivot не требуется и не поддерживается API,
            // bendy-lib опирается на направление сгиба (direction) и родительский ModelPart.
            return new BendableCuboid.Builder()
                    .setDirection(spec.direction())
                    .build(data);
        });
    }

    private static List<MutableCuboid> collectCuboids(ModelPart root) {
        List<MutableCuboid> result = new ArrayList<>();
        Set<ModelPart> visitedParts = Collections.newSetFromMap(new IdentityHashMap<>());
        Set<ModelPart.Cube> visitedCubes = Collections.newSetFromMap(new IdentityHashMap<>());
        collectPart(root, result, visitedParts, visitedCubes);
        return result;
    }

    private static void collectPart(
            ModelPart part,
            List<MutableCuboid> result,
            Set<ModelPart> visitedParts,
            Set<ModelPart.Cube> visitedCubes
    ) {
        if (part == null || !visitedParts.add(part)) {
            return;
        }

        if (part instanceof EMFModelPartWithState emfPart) {
            emfPart.allKnownStateVariants.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> collectState(entry.getValue(), result, visitedParts, visitedCubes));
            return;
        }

        collectCurrentPart(part, result, visitedParts, visitedCubes);
    }

    private static void collectState(
            EMFModelPartWithState.EMFModelState state,
            List<MutableCuboid> result,
            Set<ModelPart> visitedParts,
            Set<ModelPart.Cube> visitedCubes
    ) {
        addCubes(state.cuboids(), result, visitedCubes);
        state.variantChildren().entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(entry -> collectPart(entry.getValue(), result, visitedParts, visitedCubes));
    }

    private static void collectCurrentPart(
            ModelPart part,
            List<MutableCuboid> result,
            Set<ModelPart> visitedParts,
            Set<ModelPart.Cube> visitedCubes
    ) {
        addCubes(ModelPartAccessor.getCuboids(part), result, visitedCubes);
        ModelPartAccessor.getChildren(part).entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(entry -> collectPart(entry.getValue(), result, visitedParts, visitedCubes));
    }

    private static void addCubes(
            List<ModelPart.Cube> cubes,
            List<MutableCuboid> result,
            Set<ModelPart.Cube> visitedCubes
    ) {
        for (ModelPart.Cube cube : cubes) {
            if (visitedCubes.add(cube) && cube instanceof MutableCuboid mutable) {
                result.add(mutable);
            }
        }
    }

    private record BendSpec(Direction direction, boolean cape) {
    }
}