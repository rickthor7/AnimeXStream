package net.xblacky.animexstream.utils.realm

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration
import timber.log.Timber

class Migration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        Timber.e("Realm Migration Running")
        val schema = realm.schema
        var version: Long = oldVersion

        if (version == 0L) {
            Timber.e("Inside Migration version 0")
            realm.delete("Content")

            schema.create("EpisodeInfo")
                .addField(
                    "episodeUrl",
                    String::class.java,
                    FieldAttribute.PRIMARY_KEY,
                    FieldAttribute.REQUIRED
                )
                .addField("vidCdnUrl", String::class.java, FieldAttribute.REQUIRED)
                .addField("nextEpisodeUrl", String::class.java)
                .addField("previousEpisodeUrl", String::class.java)

            schema.create("VidCdnModel")
                .addField(
                    "vidCdnId",
                    String::class.java,
                    FieldAttribute.PRIMARY_KEY,
                    FieldAttribute.REQUIRED
                )
                .addField("ajaxParams", String::class.java, FieldAttribute.REQUIRED)
            Timber.e("Migration Completed")
            version++
        }
    }
}