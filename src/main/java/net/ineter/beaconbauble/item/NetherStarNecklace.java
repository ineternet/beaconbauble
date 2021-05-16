package net.ineter.beaconbauble.item;

import net.ineter.beaconbauble.BeaconBauble;
import net.ineter.beaconbauble.EntityRegistrar;
import net.ineter.beaconbauble.ItemRegistrar;
import net.ineter.beaconbauble.entity.BeaconItemEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;


public class NetherStarNecklace extends Item {
    private static final Properties ITEM_PROPERTIES = new Properties().fireResistant().rarity(Rarity.RARE).tab(BeaconBauble.CREATIVE_GROUP).stacksTo(1);
    public static final String REGISTRY_NAME = "nether_star_necklace";
    private static final ResourceLocation BEACON_BLOCK_TAG_RESOURCE_LOCATION = new ResourceLocation("minecraft", "beacon_base_blocks");

    public NetherStarNecklace() {
        super(ITEM_PROPERTIES);
    }

    public static IBeaconHandler getCapabilityProvider(ItemStack itemstack) {
        if (itemstack.getItem() != ItemRegistrar.NETHER_STAR_NECKLACE)
            return null;
        LazyOptional<IBeaconHandler> cap = itemstack.getCapability(BeaconItemCapabilityProvider.CAPABILITY_BEACON);
        if (cap.isPresent() && cap.resolve().isPresent()) {
            IBeaconHandler bc = cap.resolve().get();
            if (bc instanceof BeaconItemCapabilityProvider)
                return bc;
        }
        return null;
    }

    @Nullable
    private static Collection<BlockPos> worldCheck(int level, World world, BlockPos block) {
        Collection<BlockPos> blocks = new ArrayList<>();
        for (int x = -level; x <= level; x++)
            for (int z = -level; z <= level; z++) {
                blocks.add(block.offset(x, 0, z));
            }
        Stream<BlockPos> beaconBlocks = blocks.stream().filter(x->world.getBlockState(x).getBlock().getTags().contains(BEACON_BLOCK_TAG_RESOURCE_LOCATION));
        if (beaconBlocks.count() == Math.pow(1+2*level, 2))
            return blocks;
        return null;
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> use(World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!world.isClientSide() && hand == Hand.MAIN_HAND) {
            BeaconItemCapabilityProvider bc = (BeaconItemCapabilityProvider) getCapabilityProvider(itemstack);
            if (bc != null) {
                if (player.isCrouching()) {
                    BlockRayTraceResult raytraceresult = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.NONE);
                    switch (raytraceresult.getType()) {
                        case MISS: return ActionResult.pass(itemstack);
                        case ENTITY: return ActionResult.pass(itemstack);
                    }
                    BlockPos blockpos = raytraceresult.getBlockPos();
                    BlockState blockstate = world.getBlockState(blockpos);
                    if (blockstate.getBlock().getTags().contains(BEACON_BLOCK_TAG_RESOURCE_LOCATION)) {
                        switch (bc.getLevel()) {
                            case 0: {
                                Collection<BlockPos> consume = worldCheck(1, world, blockpos);
                                if (consume != null) {
                                    consume.forEach(x->world.setBlockAndUpdate(x, Blocks.AIR.defaultBlockState()));
                                    SoundEvent soundevent = SoundEvents.BEACON_ACTIVATE;
                                    player.playSound(soundevent, 1, 1);
                                    bc.setLevels(1);
                                    bc.setActive(true);
                                    return ActionResult.success(itemstack);
                                }
                                break;
                            }
                            case 1: case 2: case 3: {
                                Collection<BlockPos> consume = worldCheck(bc.getLevel()+1, world, blockpos);
                                if (consume != null) {
                                    consume.forEach(x->world.setBlockAndUpdate(x, Blocks.AIR.defaultBlockState()));
                                    bc.setLevels(bc.getLevel()+1);
                                    return ActionResult.success(itemstack);
                                }
                                break;
                            }
                        }
                        return ActionResult.pass(itemstack);
                    }
                }
                else {
                    NetworkHooks.openGui((ServerPlayerEntity) player, bc);
                    return ActionResult.success(itemstack);
                }
            }
        }

        return ActionResult.pass(itemstack);
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);

        if (!p_77663_2_.isClientSide());
            //System.out.println(getCapabilityProvider(p_77663_1_).getPrimaryEffect());
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        ItemEntity original = (ItemEntity) location;
        original.kill();
        BeaconItemEntity entity = new BeaconItemEntity(EntityRegistrar.NETHER_STAR_NECKLACE_ENTITY_TYPE, world);
        entity.setDefaultPickUpDelay();
        entity.setPos(location.getX(), location.getY(), location.getZ());
        entity.setItem(itemstack);
        return entity;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        IBeaconHandler bc = getCapabilityProvider(stack);
        //return bc != null && bc.getLevel() < 4;
        return false;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        IBeaconHandler bc = getCapabilityProvider(stack);
        return bc != null && bc.isActivated();
    }

    @Override
    public boolean canBeHurtBy(@Nonnull DamageSource ignored) {
        return false;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        BeaconItemCapabilityProvider bcp = new BeaconItemCapabilityProvider(stack);
        return bcp;
        //return new BeaconItemCapabilityProvider(stack);
    }
}
