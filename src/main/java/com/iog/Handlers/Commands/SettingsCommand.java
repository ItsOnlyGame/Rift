package com.iog.Handlers.Commands;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SettingsCommand extends BaseCommand {

    private List<Permission> requiredPermissions;
    public SettingsCommand(String[] aliases, String description, Permission[] requiredPermissions) {
        super(aliases, description);

        this.requiredPermissions = new ArrayList<>();
        if (requiredPermissions != null) {
            this.requiredPermissions.addAll(Arrays.asList(requiredPermissions));
        }
    }

    @Override
    public void execute(Message message, String[] args) {
        Member member = message.getAuthorAsMember().block();
        assert member != null;

        PermissionSet set = member.getBasePermissions().block();
        assert set != null;

        for (Permission p : requiredPermissions) {
            if (!set.contains(p)) {
                message.getChannel().subscribe(channel -> channel.createMessage("You don't have permissions to do that!").subscribe());
                return;
            }
        }

        super.execute(message, args);
    }

    @Override
    public abstract void run(Message message, String[] args);

}
