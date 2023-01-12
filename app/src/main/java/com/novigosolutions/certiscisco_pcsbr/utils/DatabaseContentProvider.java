package com.novigosolutions.certiscisco_pcsbr.utils;

import com.activeandroid.Configuration;
import com.activeandroid.content.ContentProvider;
import com.novigosolutions.certiscisco_pcsbr.models.Bags;
import com.novigosolutions.certiscisco_pcsbr.models.Box;
import com.novigosolutions.certiscisco_pcsbr.models.BoxBag;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Break;
import com.novigosolutions.certiscisco_pcsbr.models.Cage;
import com.novigosolutions.certiscisco_pcsbr.models.ChatMessage;
import com.novigosolutions.certiscisco_pcsbr.models.CoinSeries;
import com.novigosolutions.certiscisco_pcsbr.models.Consignment;
import com.novigosolutions.certiscisco_pcsbr.models.ConsignmentBag;
import com.novigosolutions.certiscisco_pcsbr.models.Currency;
import com.novigosolutions.certiscisco_pcsbr.models.Delivery;
import com.novigosolutions.certiscisco_pcsbr.models.Envelope;
import com.novigosolutions.certiscisco_pcsbr.models.EnvelopeBag;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.models.Reschedule;
import com.novigosolutions.certiscisco_pcsbr.models.UserLogs;
import com.novigosolutions.certiscisco_pcsbr.models.Wagon;

public class DatabaseContentProvider extends ContentProvider {

    @Override
    protected Configuration getConfiguration() {
        Configuration.Builder builder = new Configuration.Builder(getContext());
        builder.addModelClass(Job.class);
        builder.addModelClass(Bags.class);
        builder.addModelClass(Box.class);
        builder.addModelClass(Envelope.class);
        builder.addModelClass(EnvelopeBag.class);
        builder.addModelClass(Currency.class);
        builder.addModelClass(BoxBag.class);
        builder.addModelClass(Branch.class);
        builder.addModelClass(Delivery.class);
        builder.addModelClass(ChatMessage.class);
        builder.addModelClass(Break.class);
        builder.addModelClass(Reschedule.class);
        builder.addModelClass(CoinSeries.class);
        builder.addModelClass(Wagon.class);
        builder.addModelClass(Consignment.class);
        builder.addModelClass(ConsignmentBag.class);
        builder.addModelClass(Cage.class);
        builder.addModelClass(UserLogs.class);
        return builder.create();
    }
}