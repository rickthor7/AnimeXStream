package net.xblacky.animexstream.utils.realm

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

class Migration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema
        var version: Long = oldVersion

        if (oldVersion == 0L) {
            realm.delete("Content")

            schema.create("EpisodeInfo")
                .addField("episodeUrl", String::class.java, FieldAttribute.PRIMARY_KEY)
                .addField("vidCdnUrl", String::class.java)
                .addField("nextEpisodeUrl", String::class.java)
                .addField("previousEpisodeUrl", String::class.java)

            schema.create("VidCdnModel")
                .addField("vidCdnId", String::class.java, FieldAttribute.PRIMARY_KEY)
                .addField("ajaxParams", String::class.java, FieldAttribute.PRIMARY_KEY)
            version++
        }
    }
}